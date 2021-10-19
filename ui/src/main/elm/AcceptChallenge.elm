module AcceptChallenge exposing (..)

import Api exposing (ApiError(..), RegisteredPlayer, expectStringWithErrorHandling)
import Base
import Browser.Navigation as Nav
import Navigation
import Element as Ui
import Element.Input as Input
import Http
import Json.Encode
import CommonTypes exposing (GameMode)
import CommonTypes exposing (GameMode(..))


type Msg
    = NoOp
    | AcceptChallengeButtonClicked



type alias Model =
    { challengeId : String
    , opponentNickname : String
    , challengeMode : GameMode
    , appointment : String
    , comment : String
    , apiFailure : Maybe String
    , key : Nav.Key
    }


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
            ( model, performAcceptChallenge model )


performAcceptChallenge : Model -> Cmd Msg
performAcceptChallenge model =
    Cmd.none
    --Http.post
    --    { url = "/api/challenge/"++model.challengeId++"/accept"
    --    , expect = expectStringWithErrorHandling GotPerformAcceptChallengeResponse
    --    , body = Http.jsonBody (asChallengeRequest model)
    --    }

initPage : String -> Cmd Msg
initPage challengeId =
    Cmd.none


viewChallenge : Model -> List (Ui.Element Msg)
viewChallenge model =
    [ Ui.row
        [ Ui.width Ui.fill
        , Ui.alignTop, Ui.paddingXY 20 10
        ]
        [ viewChallengeHeader model.opponentNickname
        ]
    , Ui.row
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
    Ui.text (opponent ++ " challenged you to:")


viewChallengeDetail model =
    Ui.column
        [ Ui.width Ui.fill, Ui.alignTop, Ui.paddingXY 20 10 ]
        [ Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewGameMode model.challengeMode)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewAppointmentInput model.appointment)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewCommentInput model.comment)
        , Base.button { isDisabled = False, label = "Accept Challenge" } AcceptChallengeButtonClicked
        ]


viewGameMode mode =
    case mode of
        Duel -> Ui.text "Duel"
        TwoVsTwo -> Ui.text "2v2"
        WipeOut -> Ui.text "WipeOut"
        CTF -> Ui.text "CTF"

viewAppointmentInput appointment =
    Ui.row [ Ui.width Ui.fill ] [Ui.text "Appointment:", Ui.text appointment]


viewCommentInput comment =
    Ui.el [ Ui.width Ui.fill ] (Ui.text comment)

viewPendingChallengesTable : Model -> Ui.Element Msg
viewPendingChallengesTable _ =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0, Ui.alignTop ]
        [ Ui.text "pending challenges should go here" ]

