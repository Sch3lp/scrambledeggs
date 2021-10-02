module Home exposing (..)

import Api exposing (ApiError(..), PendingChallengeEntry, expectJsonWithErrorHandling)
import Base
import Browser.Navigation as Nav
import CommonTypes exposing (GameMode(..))
import Element as Ui
import Http
import Json.Decode as D exposing (Decoder)
import Url.Builder as UrlBuilder
import Api exposing (fetchPendingChallenges)


type Msg
    = NoOp
    | GotFetchLeaderboardResponse (Result ApiError Leaderboard)
    | GotFetchPendingChallengesResponse (Result ApiError PendingChallenges)
    | ChallengeButtonClicked


type alias LeaderboardEntry =
    { rank : Maybe Int, nickname : String, playerId : String }


type alias Leaderboard =
    List LeaderboardEntry


type alias PendingChallenges =
    List PendingChallengeEntry


type alias Model =
    { leaderboard : Leaderboard
    , pendingChallenges : PendingChallenges
    , apiFailure : Maybe String
    , key : Nav.Key
    }


handleFetchPendingChallengesResponse : Model -> Result ApiError PendingChallenges -> ( Model, Cmd Msg )
handleFetchPendingChallengesResponse model result =
    case result of
        Ok refreshedPendingChallenges ->
            ( refreshedPendingChallenges
                |> asPendingChallenges model
            , Cmd.none
            )

        Err err ->
            handleApiError err model


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


setPendingChallenges : PendingChallenges -> Model -> Model
setPendingChallenges newPendingChallenges model =
    { model | pendingChallenges = newPendingChallenges }


asPendingChallenges : Model -> PendingChallenges -> Model
asPendingChallenges model newPendingChallenges =
    setPendingChallenges newPendingChallenges model


emptyModel : Nav.Key -> Model
emptyModel =
    Model [] [] Nothing


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        GotFetchLeaderboardResponse result ->
            handleFetchLeaderboardResponse model result

        GotFetchPendingChallengesResponse result ->
            handleFetchPendingChallengesResponse model result

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
        , Ui.height Ui.shrink
        , Ui.alignTop
        , Ui.spacing 16
        ]
        [ viewLeaderboardTable model
        , viewPendingChallengesTable model
        ]
    ]


viewLeaderboardTable model =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0 ]
        [ leaderboard model ]


leaderboard : Model -> Ui.Element Msg
leaderboard model =
    Base.leaderboardTable Base.palette model.leaderboard


viewPendingChallengesTable model =
    Ui.column
        [ Ui.width Ui.fill ]
        [ pendingChallengesTable model ]


pendingChallengesTable model =
    Base.pendingChallengesTable Base.palette model.pendingChallenges



-- Fetching Leaderboard


initPage =
    Cmd.batch [performFetchLeaderboard, fetchPendingChallenges GotFetchPendingChallengesResponse]


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
