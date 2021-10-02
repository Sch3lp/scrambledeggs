port module Main exposing (..)

{-| This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Api exposing (ApiError, RegisteredPlayer, expectStringWithErrorHandling)
import Base
import Browser exposing (UrlRequest)
import Browser.Navigation as Nav exposing (Key)
import Challenge
import Element as Ui
import Element.Background as Background
import Element.Font as Font
import Home exposing (Msg(..))
import Html
import Http exposing (emptyBody)
import Json.Decode as Json
import List
import OAuth
import OAuth.Implicit as OAuth
import Registration
import Security exposing (convertBytes, defaultHttpUrl)
import Url exposing (Url)
import Url.Parser as Parser exposing ((</>))
import Widget.Material.Typography as Typo



-- Main function
-- App init


main : Program (Maybe (List Int)) Model Msg
main =
    Browser.application
        { init = Maybe.map convertBytes >> init
        , view =
            \model ->
                { title = "Scramble • Diabotical Ladder", body = [ view model ] }
        , update = update
        , subscriptions = subscriptions
        , onUrlRequest = LinkClicked
        , onUrlChange = UrlChanged
        }


type Route
    = Home
    | Registration
    | Challenge OpponentId


type alias OpponentId =
    String


parseUrl : Url.Url -> Route
parseUrl url =
    case Parser.parse parseRoute url of
        Just route ->
            route

        Nothing ->
            Home


parseRoute : Parser.Parser (Route -> a) a
parseRoute =
    Parser.oneOf
        [ Parser.map Home Parser.top
        , Parser.map Registration (Parser.s "register")
        , Parser.map Challenge (Parser.s "challenge" </> Parser.string) --/challenge/<opponentid>
        ]


emptyModel route key redirectUri authFlow token =
    Model (Home.emptyModel key)
        (Registration.emptyModel key)
        (Challenge.emptyModel key)
        route
        key
        authFlow
        redirectUri
        token


initPage : Route -> Cmd Msg
initPage model =
    case model of
        Challenge opponentId ->
            Cmd.map ChallengeMsg (Challenge.initPage opponentId)

        Home ->
            Cmd.map HomeMsg Home.initPage

        Registration ->
            Cmd.map RegistrationMsg Registration.initPage


init : Maybe { state : String } -> Url -> Key -> ( Model, Cmd Msg )
init mflags url key =
    let
        redirectUri =
            { url | query = Nothing, fragment = Nothing }

        clearUrl =
            Nav.replaceUrl key (Url.toString redirectUri)

        currentRoute =
            parseUrl url

        initPageCmd =
            initPage currentRoute

        partialEmptyModel =
            emptyModel currentRoute key redirectUri
    in
    case OAuth.parseToken url of
        OAuth.Empty ->
            ( partialEmptyModel Idle Nothing
            , initPageCmd
            )

        -- It is important to set a `state` when making the authorization request
        -- and to verify it after the redirection. The state can be anything but its primary
        -- usage is to prevent cross-site request forgery; at minima, it should be a short,
        -- non-guessable string, generated on the fly.
        --
        -- We remember any previously generated state  state using the browser's local storage
        -- and give it back (if present) to the elm application upon start
        OAuth.Success { token, state } ->
            case mflags of
                Nothing ->
                    ( partialEmptyModel (Errored ErrStateMismatch) Nothing
                    , Cmd.batch [ clearUrl, initPageCmd ]
                    )

                Just flags ->
                    if state /= Just flags.state then
                        ( partialEmptyModel (Errored ErrStateMismatch) Nothing
                        , Cmd.batch [ clearUrl, initPageCmd ]
                        )

                    else
                        ( partialEmptyModel (Authorized token) (Just token)
                        , Cmd.batch
                            [ getUserInfo localKeycloakConfiguration token
                            , clearUrl
                            , initPageCmd
                            ]
                        )

        OAuth.Error error ->
            ( partialEmptyModel (Errored <| ErrAuthorization error) Nothing
            , Cmd.batch [ clearUrl, initPageCmd ]
            )



-- MODEL
-- The stuff needed for OIDC flow


type AuthFlow
    = Idle
    | Authorized OAuth.Token
    | Done UserInfo
    | Errored Error


type Error
    = ErrStateMismatch
    | ErrAuthorization OAuth.AuthorizationError
    | ErrHTTPGetUserInfo


type alias UserInfo =
    { name : String
    }


signInRequested : Model -> ( Model, Cmd Msg )
signInRequested model =
    ( { model | authFlow = Idle }
    , genRandomBytes 16
    )


getUserInfo : Configuration -> OAuth.Token -> Cmd Msg
getUserInfo { userInfoDecoder, userInfoEndpoint } token =
    Http.request
        { method = "GET"
        , body = Http.emptyBody
        , headers = OAuth.useToken token []
        , url = Url.toString userInfoEndpoint
        , expect = Http.expectJson GotUserInfo userInfoDecoder
        , timeout = Nothing
        , tracker = Nothing
        }


gotUserInfo : Model -> Result Http.Error UserInfo -> ( Model, Cmd Msg )
gotUserInfo model userInfoResponse =
    case ( userInfoResponse, model.token ) of
        ( Err _, _ ) ->
            ( { model | authFlow = Errored ErrHTTPGetUserInfo }
            , Cmd.none
            )

        ( Ok userInfo, Just token ) ->
            ( { model | authFlow = Done userInfo }
            , performExchangeTokenForCookie token
            )

        ( Ok userInfo, Nothing ) ->
            ( { model | authFlow = Done userInfo }
            , Cmd.none
            )


signOutRequested : Model -> ( Model, Cmd Msg )
signOutRequested model =
    ( { model | authFlow = Idle, token = Nothing }
    , Cmd.none
    )


performExchangeTokenForCookie token =
    Http.request
        { method = "GET"
        , headers = OAuth.useToken token []
        , url = "/api/session"
        , body = emptyBody
        , expect = expectStringWithErrorHandling GotExchangeTokenForCookieResponse
        , timeout = Nothing
        , tracker = Nothing
        }


gotFetchRegisteredPlayerInfoResponse model response =
    case response of
        Err _ ->
            ( model, Cmd.none )

        Ok [] ->
            routeToRegister model

        Ok _ ->
            routeToKnownPlayerHome model


gotExchangeTokenForCookieResponse model =
    ( model, Api.fetchRegisteredPlayerInfo GotFetchRegisteredPlayerInfoResponse )



--TODO do error handling for when the backend server can't deal with the JWT we sent
-- the Browser actually just sets the cookie we get from the backend


routeToKnownPlayerHome model =
    ( model, Cmd.none )


routeToRegister model =
    ( model, Nav.pushUrl model.key Registration.url )



-- The full application state of our app.


type alias Model =
    { homeModel : Home.Model
    , registrationModel : Registration.Model
    , challengeModel : Challenge.Model
    , currentRoute : Route
    , key : Key
    , authFlow : AuthFlow
    , redirectUri : Url
    , token : Maybe OAuth.Token
    }


setRegistrationModel : Registration.Model -> Model -> Model
setRegistrationModel newRegModel model =
    let
        tokenEnrichedRegModel =
            { newRegModel | token = model.token }
    in
    { model | registrationModel = tokenEnrichedRegModel }


setHomeModel : Home.Model -> Model -> Model
setHomeModel newHomeModel model =
    { model | homeModel = newHomeModel }


setChallengeModel : Challenge.Model -> Model -> Model
setChallengeModel newChallengeModel model =
    { model | challengeModel = newChallengeModel }



-- UPDATE


type Msg
    = RegistrationMsg Registration.Msg
    | HomeMsg Home.Msg
    | ChallengeMsg Challenge.Msg
    | RegistrationRedirectButtonClicked
    | UrlChanged Url
    | LinkClicked UrlRequest
    | GotRandomBytes (List Int)
    | GotUserInfo (Result Http.Error UserInfo)
    | SignOutRequested
    | GotFetchRegisteredPlayerInfoResponse (Result ApiError (List RegisteredPlayer))
    | GotExchangeTokenForCookieResponse (Result ApiError ())


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RegistrationMsg subMsg ->
            let
                ( newRegistrationModel, newRegistrationMsg ) =
                    Registration.update subMsg model.registrationModel
            in
            ( setRegistrationModel newRegistrationModel model
            , Cmd.map RegistrationMsg newRegistrationMsg
            )

        RegistrationRedirectButtonClicked ->
            signInRequested model

        HomeMsg subMsg ->
            let
                ( newHomeModel, newHomeMsg ) =
                    Home.update subMsg model.homeModel
            in
            ( setHomeModel newHomeModel model
            , Cmd.map HomeMsg newHomeMsg
            )

        ChallengeMsg subMsg ->
            let
                ( newChallengeModel, newChallengeMsg ) =
                    Challenge.update subMsg model.challengeModel
            in
            ( setChallengeModel newChallengeModel model
            , Cmd.map ChallengeMsg newChallengeMsg
            )

        UrlChanged url ->
            let
                route =
                    parseUrl url
            in
            ( { model | currentRoute = route }, initPage route )

        LinkClicked urlRequest ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Nav.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Nav.load href )

        GotRandomBytes bytes ->
            gotRandomBytes model bytes

        GotUserInfo resp ->
            gotUserInfo model resp

        SignOutRequested ->
            signOutRequested model

        GotFetchRegisteredPlayerInfoResponse resp ->
            gotFetchRegisteredPlayerInfoResponse model resp

        GotExchangeTokenForCookieResponse _ ->
            gotExchangeTokenForCookieResponse model



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    randomBytes GotRandomBytes


{-| OAuth configuration.
Note that this demo also fetches basic user information with the obtained access token,
hence the user info endpoint and JSON decoder
-}
type alias Configuration =
    { authorizationEndpoint : Url
    , userInfoEndpoint : Url
    , userInfoDecoder : Json.Decoder UserInfo
    , clientId : String
    , scope : List String
    , endSessionEndpoint : Url
    }


localKeycloakConfiguration : Configuration
localKeycloakConfiguration =
    let
        issuerUrl =
            "/auth/realms/scrambled"

        authorization_endpoint =
            issuerUrl ++ "/protocol/openid-connect/auth"

        userinfo_endpoint =
            issuerUrl ++ "/protocol/openid-connect/userinfo"

        end_session_endpoint =
            issuerUrl ++ "/protocol/openid-connect/logout"
    in
    { authorizationEndpoint =
        { defaultHttpUrl | host = "localhost", port_ = Just 7070, path = authorization_endpoint }
    , userInfoEndpoint =
        { defaultHttpUrl | host = "localhost", port_ = Just 7070, path = userinfo_endpoint }
    , userInfoDecoder =
        Json.map UserInfo
            (Json.field "name" Json.string)
    , clientId =
        "scrambled-ui"
    , scope =
        [ "openid", "profile" ]
    , endSessionEndpoint =
        { defaultHttpUrl | host = "localhost", port_ = Just 7070, path = end_session_endpoint }
    }


gotRandomBytes : Model -> List Int -> ( Model, Cmd Msg )
gotRandomBytes model bytes =
    let
        { state } =
            convertBytes bytes

        authorization =
            { clientId = localKeycloakConfiguration.clientId
            , redirectUri = model.redirectUri
            , scope = localKeycloakConfiguration.scope
            , state = Just state
            , url = localKeycloakConfiguration.authorizationEndpoint
            }
    in
    ( { model | authFlow = Idle }
    , authorization
        |> OAuth.makeAuthorizationUrl
        |> Url.toString
        |> appendDummyNonce
        |> Nav.load
    )


appendDummyNonce s =
    s ++ "&nonce=1234"


port genRandomBytes : Int -> Cmd msg


port randomBytes : (List Int -> msg) -> Sub msg



-- VIEW


view : Model -> Html.Html Msg
view model =
    Ui.layout
        [ Background.color Base.palette.bg
        ]
        (Ui.column
            [ Ui.height Ui.fill
            , Ui.width Ui.fill
            , Ui.centerX
            , Ui.alignTop
            , Font.color Base.palette.font
            ]
         <|
            viewHeader model
                ++ viewMainContent model
                ++ viewFooter model
        )


viewRegistrationRedirectButton : Model -> List (Ui.Element Msg)
viewRegistrationRedirectButton model =
    if model.currentRoute == Home then
        [ Ui.row
            [ Ui.width Ui.fill
            , Ui.height Ui.fill
            , Ui.alignTop
            , Ui.centerX
            ]
            [ Ui.column
                [ Ui.width Ui.fill
                , Ui.height Ui.shrink
                , Ui.alignBottom
                ]
                [ registrationRedirectButton ]
            ]
        ]

    else
        []


registrationRedirectButton =
    Base.button
        { isDisabled = False
        , label = "Register / Log in"
        }
        RegistrationRedirectButtonClicked


viewHeader model =
    [ viewAuthInfo model.authFlow
    , Ui.el
        ([ Ui.alignTop
         , Ui.centerX
         , Ui.centerY
         , Ui.width Ui.fill
         , Background.color <| Base.contrastedPalette.bg
         , Font.color <| Base.contrastedPalette.font
         , Ui.padding 25
         ]
            ++ Typo.h1
        )
        header
    ]


header =
    Ui.text "Scramblede.gg"


viewAuthInfo : AuthFlow -> Ui.Element Msg
viewAuthInfo authFlow =
    let
        authInfo =
            case authFlow of
                Errored (ErrAuthorization errorRecord) ->
                    Maybe.withDefault "" errorRecord.errorDescription
                        |> Ui.text

                Done userInfo ->
                    Ui.row []
                        [ "Welcome "
                            ++ userInfo.name
                            ++ ". Happy Fraggin'!"
                            |> Ui.text
                        , Base.button
                            { isDisabled = False
                            , label = "Log out"
                            }
                            SignOutRequested
                        ]

                _ ->
                    Ui.text ""
    in
    authInfo


viewFooter _ =
    [ Ui.el
        [ Ui.alignBottom
        , Ui.centerX
        , Ui.width Ui.fill
        , Background.color <| Base.contrastedPalette.bg
        , Font.color <| Base.contrastedPalette.font
        , Font.size 14
        , Ui.paddingXY 16 32
        ]
        footer
    ]


footer : Ui.Element msg
footer =
    Ui.text "Diabotical District -- Where passionate trashy nerds align their goals"


viewMainContent : Model -> List (Ui.Element Msg)
viewMainContent model =
    let
        mainContentWrapper =
            Ui.column
                [ Ui.paddingXY 0 5
                , Ui.height Ui.fill
                , Ui.width Ui.fill
                ]

        mainContent =
            case model.currentRoute of
                Registration ->
                    Registration.viewRegistration model.registrationModel
                        |> List.map (Ui.map RegistrationMsg)

                Challenge opponentId ->
                    let
                        challengeModel =
                            model.challengeModel
                    in
                    Challenge.viewChallenge { challengeModel | opponentId = opponentId }
                        |> List.map (Ui.map ChallengeMsg)

                Home ->
                    Home.viewHome model.homeModel
                        |> List.map (Ui.map HomeMsg)
    in
    [ mainContentWrapper <| mainContent ++ viewRegistrationRedirectButton model ]
