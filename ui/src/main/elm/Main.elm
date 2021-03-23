module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Api exposing (ApiError(..))
import Base
import Browser
import Element as Ui
import Element.Background as Background
import Element.Font as Font
import Html
import Http
import Json.Decode as D exposing (Decoder)
import List
import Registration exposing (RegisteredPlayer, RegistrationState(..), viewRegistration)
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
    { homeModel : HomeModel
    , registrationModel : Registration.Model
    }


setRegistrationModel : Registration.Model -> Model -> Model
setRegistrationModel newRegModel model =
    { model | registrationModel = newRegModel }


setHomeModel : HomeModel -> Model -> Model
setHomeModel newHomeModel model =
    { model | homeModel = newHomeModel }


asHomeModelIn : Model -> HomeModel -> Model
asHomeModelIn model newHomeModel =
    setHomeModel newHomeModel model


emptyModel =
    { homeModel = emptyHomeModel
    , registrationModel = Registration.emptyModel
    }


type alias HomeModel =
    { leaderboard : Leaderboard
    , apiFailure : Maybe String
    }


setApiFailure : String -> HomeModel -> HomeModel
setApiFailure str homeModel =
    { homeModel | apiFailure = Just str }


asApiFailureIn : HomeModel -> String -> HomeModel
asApiFailureIn homeModel str =
    setApiFailure str homeModel


setLeaderboard : Leaderboard -> HomeModel -> HomeModel
setLeaderboard newLeaderboard homeModel =
    { homeModel | leaderboard = newLeaderboard }


asLeaderboardIn : HomeModel -> Leaderboard -> HomeModel
asLeaderboardIn homeModel newLeaderboard =
    setLeaderboard newLeaderboard homeModel


emptyHomeModel : HomeModel
emptyHomeModel =
    { leaderboard = []
    , apiFailure = Nothing
    }



-- UPDATE


type Msg
    = NoOp
    | RegistrationRedirectButtonClicked
    | GotFetchLeaderboardResponse (Result ApiError Leaderboard)
    | RegistrationMsg Registration.Msg


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RegistrationMsg regMsg ->
            let
                ( newRegistrationModel, newRegistrationMsg ) =
                    Registration.update regMsg model.registrationModel
            in
            ( setRegistrationModel newRegistrationModel model
            , Cmd.map RegistrationMsg newRegistrationMsg
            )

        RegistrationRedirectButtonClicked ->
            ( model, Cmd.none )

        GotFetchLeaderboardResponse result ->
            handleFetchLeaderboardResponse model result

        NoOp ->
            ( model, Cmd.none )



-- HTTP requests & helper functions


type alias ApiError =
    Api.ApiError


expectJsonWithErrorHandling =
    Api.expectJsonWithErrorHandling


handleApiError : ApiError -> Model -> ( Model, Cmd msg )
handleApiError err model =
    let
        failureStr =
            case err of
                BadRequest str ->
                    str

                NetworkError ->
                    "Network Error"

                Timeout ->
                    "Timeout"

                BadUrl str ->
                    str

        updatedModel =
            failureStr
                |> asApiFailureIn model.homeModel
                |> asHomeModelIn model
    in
    ( updatedModel, Cmd.none )



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
            ( newlyFetchedLeaderboard
                |> asLeaderboardIn model.homeModel
                |> asHomeModelIn model
            , Cmd.none
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


viewMainContent : Model -> List (Ui.Element Msg)
viewMainContent model =
    viewRegistration model.registrationModel
        |> List.map (Ui.map RegistrationMsg)


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



-- TODO
-- * [x] Extract button helper function so that our buttons will always look the same
-- * [ ] Split up Main.elm into a registration page and an anonymous home page
-- * [ ] Fetch both the registeredPlayers and the leaderboard at the same time; look at Task thing in Elm again
-- * [ ] Replace our own palette with that of Material somehow
-- * [ ] Extract the API stuff (fetching players, fetching leaderboard, registering new player) into its own module
-- * [ ] Splitting Api calls from a component/module is interesting if we can unit test the model without having to worry about actually performing http
