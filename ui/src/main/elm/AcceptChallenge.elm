module AcceptChallenge exposing (..)

import Api exposing (ApiError(..), PendingChallengeDetail, PendingChallengeEntry, RegisteredPlayer, performAcceptChallenge, performFetchPendingChallengeDetail)
import Base
import Browser.Navigation as Nav
import Navigation exposing (redirectToHome)
import Element as Ui
import CommonTypes exposing (GameMode)
import CommonTypes exposing (GameMode(..))


type Msg
    = NoOp
    | GotPerformFetchChallengeResponse (Result ApiError PendingChallengeDetail)
    | GotPerformAcceptChallengeResponse (Result ApiError ())
    | AcceptChallengeButtonClicked

type alias Model =
    { challengeId : String
    , challengeText : String
    , challengeMode : GameMode
    , appointment : String
    , comment : String
    , apiFailure : Maybe String
    , key : Nav.Key
    }

setChallengeId : String -> Model -> Model
setChallengeId updatedChallengeId model =
    { model | challengeId = updatedChallengeId }

setChallengeText : String -> Model -> Model
setChallengeText challengeText model =
    { model | challengeText = challengeText }

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
                    Ok pendingChallengeDetail -> updateModelWith model pendingChallengeDetail
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
    |> setChallengeText pendingChallenge.challengeText
    |> setChallengeMode pendingChallenge.gameMode
    |> setAppointment pendingChallenge.appointment
    |> setComment pendingChallenge.comment


initPage : String -> Cmd Msg
initPage challengeId =
    performFetchPendingChallengeDetail challengeId GotPerformFetchChallengeResponse


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


viewChallengeHeader challengeText =
    Ui.text (challengeText ++ " to")


viewChallengeDetail : Model -> Ui.Element Msg
viewChallengeDetail model =
    Ui.column
        [ Ui.width Ui.fill, Ui.alignTop, Ui.paddingXY 20 10 ]
        [ Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewChallengeHeader model.challengeText)
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

