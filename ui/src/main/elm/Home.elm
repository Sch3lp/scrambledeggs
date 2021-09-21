module Home exposing (..)

import Api exposing (ApiError(..), expectJsonWithErrorHandling)
import Base
import Browser.Navigation as Nav
import Element as Ui
import Element.Font as Font
import Http
import Json.Decode as D exposing (Decoder)
import String exposing (fromInt)
import Url.Builder as UrlBuilder
import Widget
import Widget.Material as Material


type Msg
    = NoOp
    | GotFetchLeaderboardResponse (Result ApiError Leaderboard)
    | ChallengeButtonClicked


type alias LeaderboardEntry =
    { rank : Maybe Int, nickname : String, playerId: String }


type alias Leaderboard =
    List LeaderboardEntry


type alias Model =
    { leaderboard : Leaderboard
    , apiFailure : Maybe String
    , key : Nav.Key
    }


setApiFailure : String -> Model -> Model
setApiFailure str homeModel =
    { homeModel | apiFailure = Just str }


asApiFailureIn : Model -> String -> Model
asApiFailureIn homeModel str =
    setApiFailure str homeModel


setLeaderboard : Leaderboard -> Model -> Model
setLeaderboard newLeaderboard homeModel =
    { homeModel | leaderboard = newLeaderboard }


asLeaderboardIn : Model -> Leaderboard -> Model
asLeaderboardIn homeModel newLeaderboard =
    setLeaderboard newLeaderboard homeModel


emptyModel : Nav.Key -> Model
emptyModel =
    Model [] Nothing


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        GotFetchLeaderboardResponse result ->
            handleFetchLeaderboardResponse model result

        ChallengeButtonClicked ->
            let
                urlCmd =
                    Nav.pushUrl model.key <| UrlBuilder.relative [ "challenge", "8af2cea4-8830-4b57-b5d5-382721e72b1b" ] []
            in
            ( model, urlCmd )


viewHome : Model -> List (Ui.Element Msg)
viewHome model =
    [ Ui.row
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.alignTop
        , Ui.spacing 16
        ]
        [ viewLeaderboardTable model
        , viewRecentMatchesTable model
        ]
    ]



viewLeaderboardTable model =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0 ]
        [ leaderboard model ]


leaderboard : Model -> Ui.Element Msg
leaderboard model =
    Base.leaderboardTable Base.palette model.leaderboard

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



-- Fetching Leaderboard


initPage =
    performFetchLeaderboard


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
    D.map3 LeaderboardEntry
        (D.maybe (D.field "rank" D.int))
        (D.field "nickname" D.string)
        (D.field "playerId" D.string)


handleFetchLeaderboardResponse : Model -> Result ApiError Leaderboard -> ( Model, Cmd Msg )
handleFetchLeaderboardResponse model result =
    case result of
        Ok newlyFetchedLeaderboard ->
            ( newlyFetchedLeaderboard
                |> asLeaderboardIn model
            , Cmd.none
            )

        Err err ->
            handleApiError err model


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

                NotAuthorized ->
                    "Unauthorized! ILLEGAL!"

        updatedModel =
            failureStr
                |> asApiFailureIn model
    in
    ( updatedModel, Cmd.none )
