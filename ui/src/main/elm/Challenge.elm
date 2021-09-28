module Challenge exposing (..)

import Api exposing (ApiError(..), RegisteredPlayer, expectJsonWithErrorHandling, expectStringWithErrorHandling)
import Base
import Browser.Navigation as Nav
import Navigation
import Element as Ui
import Element.Input as Input
import Http
import Json.Decode as D exposing (Decoder)
import Json.Encode
import Widget
import Widget.Material as Material
import CommonTypes exposing (GameMode)
import CommonTypes exposing (GameMode(..))


type Msg
    = NoOp
    | GotFetchPendingChallengesResponse (Result ApiError PendingChallenges)
    | GotFetchPlayerResponse (Result ApiError RegisteredPlayer)
    | GotFetchRegisteredPlayerInfoResponse (Result ApiError (List RegisteredPlayer))
    | GotPerformChallengeResponse (Result ApiError ())
    | GameModeChosen GameMode
    | AppointmentChanged String
    | CommentChanged String
    | ChallengeButtonClicked


type alias PendingChallenge =
    { challengeText : String }


type alias PendingChallenges =
    List PendingChallenge





type alias PlayerId =
    String


type alias Model =
    { challengerId : PlayerId
    , opponentId : PlayerId
    , opponentNickname : String
    , challengeMode : GameMode
    , appointment : String
    , comment : String
    , pendingChallenges : PendingChallenges
    , apiFailure : Maybe String
    , key : Nav.Key
    }


setApiFailure : String -> Model -> Model
setApiFailure str model =
    { model | apiFailure = Just str }


asApiFailureIn : Model -> String -> Model
asApiFailureIn model str =
    setApiFailure str model


setChallengeMode : GameMode -> Model -> Model
setChallengeMode selectedChallengeMode model =
    { model | challengeMode = selectedChallengeMode }


setAppointment : String -> Model -> Model
setAppointment updatedAppointment model =
    { model | appointment = updatedAppointment }


setComment : String -> Model -> Model
setComment updatedComment model =
    { model | comment = updatedComment }


setPendingChallenges : PendingChallenges -> Model -> Model
setPendingChallenges newPendingChallenges model =
    { model | pendingChallenges = newPendingChallenges }


asPendingChallenges : Model -> PendingChallenges -> Model
asPendingChallenges model newPendingChallenges =
    setPendingChallenges newPendingChallenges model


emptyModel : Nav.Key -> Model
emptyModel =
    Model "" "" "" Duel "" "" [] Nothing


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        GotFetchPendingChallengesResponse result ->
            handleFetchPendingChallengesResponse model result

        ChallengeButtonClicked ->
            ( model, performChallenge model )

        GameModeChosen gameMode ->
            ( setChallengeMode gameMode model, Cmd.none )

        AppointmentChanged updatedAppointment ->
            ( setAppointment updatedAppointment model, Cmd.none )

        CommentChanged updatedComment ->
            ( setComment updatedComment model, Cmd.none )

        GotFetchPlayerResponse result ->
            handleFetchPlayerResponse model result

        GotFetchRegisteredPlayerInfoResponse resp ->
            gotFetchRegisteredPlayerInfoResponse model resp

        GotPerformChallengeResponse result ->
            case result of
                Ok _ -> Navigation.redirectToHome <| emptyModel model.key
                _ -> (model, Cmd.none)



performChallenge : Model -> Cmd Msg
performChallenge model =
    Http.post
        { url = "/api/challenge"
        , expect = expectStringWithErrorHandling GotPerformChallengeResponse
        , body = Http.jsonBody (asChallengeRequest model)
        }


asChallengeRequest : Model -> Json.Encode.Value
asChallengeRequest model =
    Json.Encode.object
        [ ( "challenger", Json.Encode.string model.challengerId )
        , ( "opponent", Json.Encode.string model.opponentId )
        , ( "comment", Json.Encode.string model.comment )
        , ( "appointmentSuggestion", Json.Encode.string model.appointment )
        , ( "gameMode", Json.Encode.string <| challengeModeAsString model.challengeMode )
        ]


challengeModeAsString : GameMode -> String
challengeModeAsString gameMode =
    case gameMode of
        Duel ->
            "Duel"

        TwoVsTwo ->
            "TwoVsTwo"

        WipeOut ->
            "WipeOut"

        CTF ->
            "CTF"


initPage : PlayerId -> Cmd Msg
initPage opponentId =
    Cmd.batch
        [ Api.fetchRegisteredPlayerInfo GotFetchRegisteredPlayerInfoResponse
        , Api.fetchRegisteredPlayer opponentId GotFetchPlayerResponse
        ]


handleFetchPlayerResponse : Model -> Result ApiError RegisteredPlayer -> ( Model, Cmd Msg )
handleFetchPlayerResponse model result =
    case result of
        Ok newlyFetchedPlayer ->
            ( { model
                | opponentNickname = newlyFetchedPlayer.nickname
                , opponentId = newlyFetchedPlayer.playerId
              }
            , Cmd.none
            )

        Err err ->
            handleApiError err model


gotFetchRegisteredPlayerInfoResponse model response =
    case response of
        Ok (result :: []) ->
            ( { model | challengerId = result.playerId }, Cmd.none )

        Ok _ ->
            -- [], [1,2], [1,2,3]
            ( model, Cmd.none )

        Err _ ->
            ( model, Cmd.none )


viewChallenge : Model -> List (Ui.Element Msg)
viewChallenge model =
    [ Ui.row
        [ Ui.width Ui.fill
        , Ui.alignTop
        ]
        [ viewChallengeHeader model.opponentNickname
        ]
    , Ui.row
        [ Ui.width Ui.fill
        , Ui.height Ui.fill
        , Ui.alignTop
        , Ui.spacing 16
        ]
        [ viewChallengeMode model.challengeMode
        , viewChallengeDetail model
        , viewPendingChallengesTable model
        ]
    ]


viewChallengeHeader opponent =
    Ui.text ("Let's set you up vs. " ++ opponent)


viewChallengeMode selectedChallengeMode =
    let
        component =
            Input.radio
                [ Ui.padding 10
                , Ui.spacing 20
                ]
                { onChange = GameModeChosen
                , selected = Just selectedChallengeMode
                , label = Input.labelHidden "Mode"
                , options =
                    [ Input.option Duel (Ui.text "Duel")
                    , Input.option TwoVsTwo (Ui.text "2v2")
                    , Input.option WipeOut (Ui.text "WipeOut")
                    , Input.option CTF (Ui.text "CTF")
                    ]
                }
    in
    Ui.column
        [ Ui.width Ui.fill, Ui.alignTop, Ui.paddingXY 20 10 ]
        [ component ]


viewChallengeDetail model =
    Ui.column
        [ Ui.width Ui.fill, Ui.alignTop, Ui.paddingXY 20 10 ]
        [ Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewAppointmentInput model.appointment)
        , Ui.el [ Ui.width Ui.fill, Ui.paddingXY 0 5 ] (viewCommentInput model.comment)
        , Base.button { isDisabled = False, label = "Challenge" } ChallengeButtonClicked
        ]


viewAppointmentInput appointment =
    let
        placeholder =
            Just (Input.placeholder [] (Ui.text "Next wednesday at 20:00"))
    in
    Input.text [ Ui.width Ui.fill ]
        { label = Input.labelHidden "Appointment"
        , placeholder = placeholder
        , text = appointment
        , onChange = AppointmentChanged
        }


viewCommentInput comment =
    let
        placeholder =
            Just (Input.placeholder [] (Ui.text "Add me on Discord via Eggbot#1234"))
    in
    Input.multiline [ Ui.width Ui.fill ]
        { label = Input.labelHidden "Comment"
        , placeholder = placeholder
        , text = comment
        , onChange = CommentChanged
        , spellcheck = False
        }


viewPendingChallengesTable : Model -> Ui.Element Msg
viewPendingChallengesTable model =
    Ui.column
        [ Ui.width Ui.fill, Ui.paddingXY 20 0, Ui.alignTop ]
        [ pendingChallenges model ]


pendingChallenges : Model -> Ui.Element Msg
pendingChallenges model =
    Widget.sortTable (Material.sortTable Material.defaultPalette)
        { content = model.pendingChallenges
        , columns =
            [ Widget.stringColumn
                { title = "Pending"
                , value = .challengeText
                , toString = identity
                , width = Ui.fill
                }
            ]
        , asc = True
        , sortBy = "None"
        , onChange = \_ -> NoOp
        }


rankToString : Maybe Int -> String
rankToString maybeInt =
    maybeInt
        |> Maybe.map (\int -> "#" ++ String.fromInt int)
        |> Maybe.withDefault ""



-- Fetching Pending Challenges


performFetchPendingChallenges =
    Http.get
        { url = "/api/challenges"
        , expect = expectJsonWithErrorHandling pendingChallengesDecoder GotFetchPendingChallengesResponse
        }


pendingChallengesDecoder : Decoder PendingChallenges
pendingChallengesDecoder =
    D.list pendingChallengeDecoder


pendingChallengeDecoder : Decoder PendingChallenge
pendingChallengeDecoder =
    D.map PendingChallenge
        (D.field "challengeText" D.string)


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
