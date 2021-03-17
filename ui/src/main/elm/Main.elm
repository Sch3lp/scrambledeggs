module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Base
import Browser
import Element as Ui
import Element.Background as Background
import Element.Font as Font
import Element.Input as Input
import Html
import Html.Events
import Http
import Json.Decode as D exposing (Decoder)
import Json.Encode
import List
import Url
import Url.Parser as Parser
import Widget
import Widget.Material as Material
import Widget.Material.Typography as Typo



-- Main function
-- App init


main : Program (Maybe String) Model Msg
main =
    Browser.document
        { init = init
        , view =
            \model ->
                { title = "Scramble • Diabotical Ladder", body = [ view model ] }
        , update = update
        , subscriptions = subscriptions
        }


init : Maybe String -> ( Model, Cmd Msg )
init _ =
    ( emptyModel
    , performFetchLeaderboard
    )



-- MODEL
-- The full application state of our app.


type alias LeaderboardEntry =
    { rank : Maybe Int, nickname : String }


type alias Leaderboard =
    List LeaderboardEntry


type alias Model =
    { leaderboard : Leaderboard
    , registeredPlayers : List RegisteredPlayer
    , registrationStatus : RegistrationState
    , registerInput : String
    }


initialLeaderboard : Leaderboard
initialLeaderboard =
    [ { rank = Just 1, nickname = "Sch3lp" }
    , { rank = Just 2, nickname = "CoreDusk" }
    , { rank = Just 3, nickname = "ElFips" }
    , { rank = Nothing, nickname = "Evsie" }
    ]


emptyModel : Model
emptyModel =
    { leaderboard = []
    , registeredPlayers = []
    , registrationStatus = NotRegistered
    , registerInput = ""
    }



-- Types


type RegistrationState
    = NotRegistered
    | Registered
    | Failed String
    | CallingAPI


type alias RegisteredPlayer =
    { nickname : String

    -- Other stuff can go here, such as email, avatar
    }



-- UPDATE


type Msg
    = NoOp
    | UpdateRegisterInput String
    | RegisterButtonClicked
    | RegistrationRedirectButtonClicked
    | GotRegisterPlayerResponse (Result ApiError ())
    | GotFetchPlayersResponse (Result ApiError (List RegisteredPlayer))
    | GotFetchLeaderboardResponse (Result ApiError Leaderboard)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        UpdateRegisterInput str ->
            ( { model | registerInput = str }
            , Cmd.none
            )

        RegisterButtonClicked ->
            registerPlayer model

        RegistrationRedirectButtonClicked ->
            ( model, Cmd.none )

        -- Todo actually implement redirecting to register screen
        GotRegisterPlayerResponse result ->
            handleRegisterPlayerResponse model result

        GotFetchPlayersResponse result ->
            handleFetchPlayersResponse model result

        GotFetchLeaderboardResponse result ->
            handleFetchLeaderboardResponse model result



-- HTTP requests & helper functions


type ApiError
    = BadRequest String
    | NetworkError
    | Timeout
    | BadUrl String


expectStringWithErrorHandling : (Result ApiError () -> msg) -> Http.Expect msg
expectStringWithErrorHandling toMsg =
    Http.expectStringResponse toMsg
        (\response ->
            case response of
                Http.BadUrl_ url ->
                    Err (BadUrl url)

                Http.Timeout_ ->
                    Err Timeout

                Http.NetworkError_ ->
                    Err NetworkError

                Http.BadStatus_ metadata body ->
                    Err (BadRequest body)

                Http.GoodStatus_ metadata _ ->
                    Ok ()
        )


expectJsonWithErrorHandling : Decoder a -> (Result ApiError a -> msg) -> Http.Expect msg
expectJsonWithErrorHandling decoder toMsg =
    Http.expectStringResponse toMsg
        (\response ->
            case response of
                Http.BadUrl_ url ->
                    Err (BadUrl url)

                Http.Timeout_ ->
                    Err Timeout

                Http.NetworkError_ ->
                    Err NetworkError

                Http.BadStatus_ metadata body ->
                    Err (BadRequest body)

                Http.GoodStatus_ metadata body ->
                    case D.decodeString decoder body of
                        Ok value ->
                            Ok value

                        Err err ->
                            Err (BadRequest (D.errorToString err))
        )


handleApiError : ApiError -> Model -> ( Model, Cmd msg )
handleApiError err model =
    case err of
        BadRequest str ->
            ( { model | registrationStatus = Failed str }, Cmd.none )

        NetworkError ->
            ( { model | registrationStatus = Failed "Network Error" }, Cmd.none )

        Timeout ->
            ( { model | registrationStatus = Failed "Timeout" }, Cmd.none )

        BadUrl a ->
            ( { model | registrationStatus = Failed a }, Cmd.none )


httpErrorToString : Http.Error -> String
httpErrorToString error =
    case error of
        Http.BadUrl url ->
            "The URL " ++ url ++ " was invalid"

        Http.Timeout ->
            "Unable to reach the server, try again"

        Http.NetworkError ->
            "Unable to reach the server, check your network connection"

        Http.BadStatus 500 ->
            "The server had a problem, try again later"

        Http.BadStatus 400 ->
            "Verify your information and try again"

        Http.BadStatus x ->
            "Unknown error: " ++ String.fromInt x

        Http.BadBody errorMessage ->
            errorMessage



-- Fetching Leaderboard


performFetchLeaderboard =
    Http.get
        { url = "/api/leaderboard"
        , expect = expectJsonWithErrorHandling leaderboardDecoder GotFetchLeaderboardResponse
        }


leaderboardDecoder : Decoder Leaderboard
leaderboardDecoder =
    D.list leaderboardEntryDecoder


leaderboardEntryDecoder : Decoder LeaderboardEntry
leaderboardEntryDecoder =
    D.map2 LeaderboardEntry
        (D.maybe (D.field "rank" D.int))
        (D.field "nickname" D.string)


handleFetchLeaderboardResponse : Model -> Result ApiError Leaderboard -> ( Model, Cmd Msg )
handleFetchLeaderboardResponse model result =
    case result of
        Ok newlyFetchedLeaderboard ->
            ( { model | leaderboard = newlyFetchedLeaderboard }, Cmd.none )

        Err err ->
            handleApiError err model



-- Fetching Players


performFetchPlayers =
    Http.get
        { url = "/api/player"
        , expect = expectJsonWithErrorHandling registeredPlayersDecoder GotFetchPlayersResponse
        }


registeredPlayersDecoder : Decoder (List RegisteredPlayer)
registeredPlayersDecoder =
    D.list registeredPlayerDecoder


registeredPlayerDecoder : Decoder RegisteredPlayer
registeredPlayerDecoder =
    D.map RegisteredPlayer <|
        D.field "nickname" D.string


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
        playerNameJson =
            model.registerInput
                |> registerPlayerEncoder
    in
    ( { model | registrationStatus = CallingAPI }
    , Http.post
        { url = "/api/register"
        , body = Http.jsonBody playerNameJson
        , expect = expectStringWithErrorHandling GotRegisterPlayerResponse
        }
    )


registerPlayerEncoder : String -> Json.Encode.Value
registerPlayerEncoder name =
    Json.Encode.object [ ( "nickname", Json.Encode.string name ) ]


handleRegisterPlayerResponse : Model -> Result ApiError () -> ( Model, Cmd Msg )
handleRegisterPlayerResponse model result =
    case result of
        Ok _ ->
            ( { model | registrationStatus = Registered, registerInput = "" }
            , performFetchPlayers
            )

        Err err ->
            handleApiError err model



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- APP ROUTING
-- I don't fucking get this
-- Does nothing atm


type Route
    = Register
    | Leaderboards
    | ChallengePlayer
    | Report
    | NotFound


parseUrl : Url.Url -> Route
parseUrl url =
    case Parser.parse parseRoute url of
        Just route ->
            route

        Nothing ->
            Leaderboards


parseRoute : Parser.Parser (Route -> a) a
parseRoute =
    Parser.oneOf
        [ Parser.map Register Parser.top
        , Parser.map Leaderboards (Parser.s "leaderboards")
        , Parser.map Report (Parser.s "reports")
        ]



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


viewHeader model =
    [ Ui.el
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


viewFooter model =
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


viewMainContent model =
    viewLeaderboard model


viewLeaderboard model =
    [ Ui.row
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.alignTop
        , Ui.spacing 16
        ]
        [ viewLeaderboardTable model
        , viewRecentMatchesTable model
        ]
    , viewRegistrationRedirectButton
    ]


viewLeaderboardTable model =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0 ]
        [ leaderboard model ]


leaderboard model =
    Widget.sortTable (Material.sortTable Material.defaultPalette)
        { content = model.leaderboard
        , columns =
            [ Widget.unsortableColumn
                { title = "Rank"
                , toString = \{ rank } -> rankToString rank
                , width = Ui.fill
                }
            , Widget.stringColumn
                { title = "NickName"
                , value = .nickname
                , toString = identity
                , width = Ui.fill
                }
            ]
        , asc = True
        , sortBy = "Rank"
        , onChange = \_ -> NoOp
        }


rankToString : Maybe Int -> String
rankToString maybeInt =
    maybeInt
        |> Maybe.map (\int -> "#" ++ String.fromInt int)
        |> Maybe.withDefault ""


viewRecentMatchesTable model =
    Ui.column
        [ Ui.width Ui.fill ]
        [ recentMatchesTable model ]


recentMatchesTable model =
    Widget.sortTable (Material.sortTable Material.defaultPalette)
        { content =
            [ "Evsie 9 vs. 7 Sch3lp | Duel"
            , "Sch3lp 10 vs. 9 Evsie | Duel"
            , "MUR! 8 vs. 3 NUT5! | CTF"
            ]
        , columns =
            [ Widget.stringColumn
                { title = "Recent matches"
                , value = identity
                , toString = identity
                , width = Ui.fill
                }
            ]
        , asc = True
        , sortBy = "Recent matches"
        , onChange = \_ -> NoOp
        }


viewRegistrationRedirectButton =
    Ui.row
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.alignTop
        , Ui.centerX
        ]
        [ Ui.column [ Ui.width Ui.fill, Ui.height Ui.fill ]
            [ registrationRedirectButton False ]
        ]


registrationRedirectButton isDisabled =
    Base.button
        { isDisabled = isDisabled
        , label = "Register"
        }
        RegistrationRedirectButtonClicked


viewRegistration model =
    [ Ui.column
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.paddingEach { top = 25, left = 0, right = 0, bottom = 0 }
        , Ui.spacing 16
        ]
        (mainContent model)
    ]


mainContent model =
    [ viewRegisterInput model.registerInput
    , viewRegisterButton (model.registerInput == "")
    , viewStatusMessage model.registrationStatus
    , viewRegisteredPlayers model.registeredPlayers
    ]


viewRegisteredPlayers : List RegisteredPlayer -> Ui.Element Msg
viewRegisteredPlayers registeredPlayers =
    Ui.column [ Ui.centerX ]
        (List.map
            (\u -> Ui.row [] [ Ui.text u.nickname ])
            registeredPlayers
        )


viewRegisterInput : String -> Ui.Element Msg
viewRegisterInput value =
    Ui.el
        [ Ui.centerX ]
        (Input.text
            [ onEnter RegisterButtonClicked
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
        , label = "Register"
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



-- View helper functions


onEnter : msg -> Ui.Attribute msg
onEnter msg =
    Ui.htmlAttribute
        (Html.Events.on "keyup"
            (D.field "key" D.string
                |> D.andThen
                    (\key ->
                        if key == "Enter" then
                            D.succeed msg

                        else
                            D.fail "Not the enter key"
                    )
            )
        )



-- TODO
-- * [x] Extract button helper function so that our buttons will always look the same
-- * [ ] Extract the API stuff (fetching players, fetching leaderboard, registering new player) into its own module
-- * [ ] Fetch both the registeredPlayers and the leaderboard at the same time; look at Task thing in Elm again
-- * [ ] Split up Main.elm into a registration page and an anonymous home page
-- * [ ] Replace our own palette with that of Material somehow
