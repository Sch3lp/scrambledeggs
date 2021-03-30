module Home exposing (..)

import Api exposing (ApiError(..), expectJsonWithErrorHandling)
import Base
import Browser.Navigation as Nav
import Element as Ui
import Http
import Json.Decode as D exposing (Decoder)
import Widget
import Widget.Material as Material


type Msg
    = NoOp
    | RegistrationRedirectButtonClicked
    | GotFetchLeaderboardResponse (Result ApiError Leaderboard)


type alias LeaderboardEntry =
    { rank : Maybe Int, nickname : String }


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

        RegistrationRedirectButtonClicked ->
            ( model, Nav.pushUrl model.key "http://localhost:8000/register" )


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

        updatedModel =
            failureStr
                |> asApiFailureIn model
    in
    ( updatedModel, Cmd.none )
