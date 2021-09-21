module Registration exposing (..)

import Api exposing (ApiError(..), expectStringWithErrorHandling)
import Base
import Browser.Navigation as Nav
import Element as Ui
import Element.Font as Font
import Element.Input as Input
import Http
import Json.Encode
import OAuth
import Url.Builder
import Api exposing (RegisteredPlayer)
import Navigation


url : String
url =
    Url.Builder.relative [ "register" ] []


type Msg
    = RegisterButtonClicked
    | UpdateRegisterInput String
    | GotRegisterPlayerResponse (Result ApiError ())
    | GotFetchPlayersResponse (Result ApiError (List RegisteredPlayer))


type alias Model =
    { registerInput : String
    , registrationStatus : RegistrationState
    , registeredPlayers : List RegisteredPlayer
    , apiFailure : Maybe String
    , token : Maybe OAuth.Token
    , key : Nav.Key
    }

initPage = Cmd.none

emptyModel : Nav.Key -> Model
emptyModel =
    Model "" NotRegistered [] Nothing Nothing


type RegistrationState
    = NotRegistered
    | Registered
    | Failed String
    | CallingAPI


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        UpdateRegisterInput str ->
            ( { model | registerInput = str }
            , Cmd.none
            )

        RegisterButtonClicked ->
            registerPlayer model

        GotRegisterPlayerResponse result ->
            handleRegisterPlayerResponse model result

        GotFetchPlayersResponse result ->
            handleFetchPlayersResponse model result


viewRegistration : Model -> List (Ui.Element Msg)
viewRegistration model =
    [ Ui.column
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.paddingEach { top = 25, left = 0, right = 0, bottom = 0 }
        , Ui.spacing 16
        ]
        (mainRegistration model)
    ]


mainRegistration model =
    [ viewRegisterInput model.registerInput
    , viewRegisterButton (model.registerInput == "")
    , viewStatusMessage model.registrationStatus
    , viewRegisteredPlayers model.registeredPlayers
    ]


viewRegisterInput : String -> Ui.Element Msg
viewRegisterInput value =
    Ui.el
        [ Ui.centerX ]
        (Input.text
            [ Base.onEnter RegisterButtonClicked
            ]
            { label = Input.labelLeft [] (Ui.text "Nickname")
            , onChange = UpdateRegisterInput
            , placeholder = Just (Input.placeholder [] (Ui.text "L33tSn!per69"))
            , text = value
            }
        )



-- The docs basically say not to use disable for accessibility reasons https://package.elm-lang.org/packages/mdgriffith/elm-ui/latest/Element-Input#disabling-inputs
-- This button does nothing when disabled, otherwise sends RegisterButtonClicked msg


viewRegisterButton : Bool -> Ui.Element Msg
viewRegisterButton isDisabled =
    Base.button
        { isDisabled = isDisabled
        , label = "Join the fr(a)y"
        }
        RegisterButtonClicked


viewStatusMessage : RegistrationState -> Ui.Element Msg
viewStatusMessage registrationStatus =
    let
        sharedAttributes =
            [ Ui.width (Ui.fill |> Ui.maximum 600), Ui.centerX, Font.center ]
    in
    case registrationStatus of
        NotRegistered ->
            Ui.none

        Registered ->
            Ui.paragraph sharedAttributes
                [ Ui.text "Yay! You are now registered for the Scramble Ladder." ]

        CallingAPI ->
            Ui.paragraph sharedAttributes
                [ Ui.text "Loading" ]

        Failed errorMessage ->
            Ui.paragraph sharedAttributes
                [ Ui.text errorMessage ]


viewRegisteredPlayers : List RegisteredPlayer -> Ui.Element Msg
viewRegisteredPlayers registeredPlayers =
    Ui.column [ Ui.centerX ]
        (List.map
            (\u -> Ui.row [] [ Ui.text u.nickname ])
            registeredPlayers
        )

handleFetchPlayersResponse : Model -> Result ApiError (List RegisteredPlayer) -> ( Model, Cmd Msg )
handleFetchPlayersResponse model result =
    case result of
        Ok newlyFetchedPlayers ->
            ( { model | registeredPlayers = newlyFetchedPlayers }, Cmd.none )

        Err err ->
            handleApiError err model



-- Registering a new Player


registerPlayer : Model -> ( Model, Cmd Msg )
registerPlayer model =
    let
        registrationInfo =
            model.registerInput
                |> registrationInfoEncoder

        request =
            { method = "POST"
            , headers = []
            , url = "/api/register"
            , body = Http.jsonBody registrationInfo
            , expect = expectStringWithErrorHandling GotRegisterPlayerResponse
            , timeout = Nothing
            , tracker = Nothing
            }
    in
    ( { model | registrationStatus = CallingAPI }
    , Http.request request
    )


registrationInfoEncoder : String -> Json.Encode.Value
registrationInfoEncoder name =
    Json.Encode.object [ ( "nickname", Json.Encode.string name ) ]


handleRegisterPlayerResponse : Model -> Result ApiError () -> ( Model, Cmd Msg )
handleRegisterPlayerResponse model result =
    case result of
        Ok _ ->
            let
                refreshedModel = { model | registrationStatus = Registered, registerInput = "" }
            in
                Navigation.redirectToHome refreshedModel

        Err err ->
            handleApiError err model


handleApiError : ApiError -> Model -> ( Model, Cmd msg )
handleApiError err model =
    case err of
        BadRequest str ->
            ( { model | apiFailure = Just str }, Cmd.none )

        NetworkError ->
            ( { model | apiFailure = Just "Network Error" }, Cmd.none )

        Timeout ->
            ( { model | apiFailure = Just "Timeout" }, Cmd.none )

        BadUrl str ->
            ( { model | apiFailure = Just str }, Cmd.none )

        NotAuthorized ->
            ( { model | apiFailure = Just "Unauthorized! GO AWAY!" }, Cmd.none )
