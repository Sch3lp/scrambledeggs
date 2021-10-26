module AcceptChallenge exposing (..)

import Api exposing (ApiError(..), RegisteredPlayer, performAcceptChallenge, expectJsonWithErrorHandling, expectStringWithErrorHandling, toGameMode)
import Base
import Browser.Navigation as Nav
import Json.Decode as D exposing (Decoder)
import Navigation exposing (redirectToHome)
import Element as Ui
import Element.Input as Input
import Http exposing (emptyBody)
import Json.Encode
import CommonTypes exposing (GameMode)
import CommonTypes exposing (GameMode(..))


type Msg
    = NoOp
    | GotPerformFetchChallengeResponse (Result ApiError PendingChallenge)
    | GotPerformAcceptChallengeResponse (Result ApiError ())
    | AcceptChallengeButtonClicked

type alias PendingChallenge =
    { challengeId : String
    , opponentName : String
    , gameMode : GameMode
    , appointment : String
    , comment : String
    }

type alias Model =
    { challengeId : String
    , opponentNickname : String
    , challengeMode : GameMode
    , appointment : String
    , comment : String
    , apiFailure : Maybe String
    , key : Nav.Key
    }

setChallengeId : String -> Model -> Model
setChallengeId updatedChallengeId model =
    { model | challengeId = updatedChallengeId }

setOppentNickname : String -> Model -> Model
setOppentNickname updatedNickname model =
    { model | opponentNickname = updatedNickname }

setChallengeMode : GameMode -> Model -> Model
setChallengeMode selectedChallengeMode model =
    { model | challengeMode = selectedChallengeMode }

setAppointment : String -> Model -> Model
setAppointment updatedAppointment model =
    { model | appointment = updatedAppointment }

setComment : String -> Model -> Model
setComment updatedComment model =
    { model | comment = updatedComment }



emptyModel : Nav.Key -> Model
emptyModel =
    Model "" "" Duel "" "" Nothing


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        AcceptChallengeButtonClicked ->
            ( model, performAcceptChallenge model.challengeId GotPerformAcceptChallengeResponse )

        GotPerformFetchChallengeResponse result ->
            let
                updatedModel = case result of
                    Ok pendingChallenge -> updateModelWith model pendingChallenge
                    Err _ -> model
            in
                ( updatedModel, Cmd.none )

        GotPerformAcceptChallengeResponse result ->
            case result of
                Ok _ -> redirectToHome model
                Err _ -> (model, Cmd.none)

updateModelWith model pendingChallenge =
    model
    |> setChallengeId pendingChallenge.challengeId
    |> setOppentNickname pendingChallenge.opponentName
    |> setChallengeMode pendingChallenge.gameMode
    |> setAppointment pendingChallenge.appointment
    |> setComment pendingChallenge.comment

performFetchPendingChallenge : String -> Cmd Msg
performFetchPendingChallenge challengeId =
    Http.get
        { url = "/api/challenge/pending/"++challengeId
        , expect = expectJsonWithErrorHandling pendingChallengeDecoder GotPerformFetchChallengeResponse
        }

pendingChallengeDecoder : Decoder PendingChallenge
pendingChallengeDecoder =
    D.map5 PendingChallenge
        (D.field "challengeId" D.string)
        (D.field "opponentName" D.string)
        (D.field "gameMode" D.string |> D.andThen toGameMode)
        (D.field "appointment" D.string)
        (D.field "comment" D.string)

initPage : String -> Cmd Msg
initPage challengeId =
    performFetchPendingChallenge challengeId


viewChallenge : Model -> List (Ui.Element Msg)
viewChallenge model =
    [ Ui.row
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.alignTop
        , Ui.spacing 16
        ]
        [ viewChallengeDetail model
        , viewPendingChallengesTable model
        ]
    ]


viewChallengeHeader opponent =
    Ui.text (opponent ++ " challenged you to")


viewChallengeDetail model =
    Ui.column
        [ Ui.width Ui.fill, Ui.alignTop, Ui.paddingXY 20 10 ]
        [ Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewChallengeHeader model.opponentNickname)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewGameMode model.challengeMode)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewAppointmentInput model.appointment)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewCommentInput model.comment)
        , Base.button { isDisabled = False, label = "Accept Challenge" } AcceptChallengeButtonClicked
        ]


viewGameMode mode =
    let
        modeAsText = case mode of
            Duel -> Ui.text "Duel"
            TwoVsTwo -> Ui.text "2v2"
            WipeOut -> Ui.text "WipeOut"
            CTF -> Ui.text "CTF"
    in
        Ui.row [ Ui.width Ui.fill ] [Ui.text "a ", modeAsText]


viewAppointmentInput appointment =
    Ui.row [ Ui.width Ui.fill ] [Ui.text appointment]


viewCommentInput comment =
    Ui.el [ Ui.width Ui.fill ] (Ui.text comment)

viewPendingChallengesTable : Model -> Ui.Element Msg
viewPendingChallengesTable _ =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0, Ui.alignTop ]
        [ Ui.text "pending challenges should go here" ]

