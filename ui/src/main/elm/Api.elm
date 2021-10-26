module Api exposing (..)

import Http exposing (emptyBody)
import Json.Decode as D exposing (Decoder)
import CommonTypes exposing (GameMode,GameMode(..))


type ApiError
    = BadRequest String
    | NotAuthorized
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
                    handleBadStatus metadata body

                Http.GoodStatus_ _ _ ->
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
                    handleBadStatus metadata body

                Http.GoodStatus_ _ body ->
                    case D.decodeString decoder body of
                        Ok value ->
                            Ok value

                        Err err ->
                            Err (BadRequest (D.errorToString err))
        )


handleBadStatus : { a | statusCode : number } -> String -> Result ApiError value
handleBadStatus metadata body =
    if metadata.statusCode == 401 then
        Err NotAuthorized

    else
        Err (BadRequest body)


registeredPlayersDecoder : Decoder (List RegisteredPlayer)
registeredPlayersDecoder =
    D.list registeredPlayerDecoder

registeredPlayerDecoder : Decoder RegisteredPlayer
registeredPlayerDecoder =
    D.map2 RegisteredPlayer
        (D.field "playerId" D.string)
        (D.field "nickname" D.string)


type alias RegisteredPlayer =
    { playerId : String
    , nickname : String
    }

type alias MsgConstructor successType msg = (Result ApiError successType -> msg)

fetchRegisteredPlayerInfo : MsgConstructor (List RegisteredPlayer) msg -> Cmd msg
fetchRegisteredPlayerInfo msg =
    Http.get
        { url = "/api/player/info"
        , expect = expectJsonWithErrorHandling registeredPlayersDecoder msg
        }

fetchRegisteredPlayer : String -> MsgConstructor RegisteredPlayer msg -> Cmd msg
fetchRegisteredPlayer opponentId msg =
    Http.get
            { url = "/api/player/" ++ opponentId
            , expect = expectJsonWithErrorHandling registeredPlayerDecoder msg
            }

-- Pending Challenges

type alias PendingChallengeEntry =
    { challengeId : String
    , gameMode : GameMode
    , opponentName : String
    , appointment : String
    , comment : String
    }
    
pendingChallengesDecoder: Decoder (List PendingChallengeEntry)
pendingChallengesDecoder = 
    D.list pendingChallengeDecoder

pendingChallengeDecoder: Decoder PendingChallengeEntry
pendingChallengeDecoder = 
    D.map5 PendingChallengeEntry
        (D.field "challengeId" D.string)
        (D.field "gameMode" D.string |> D.andThen toGameMode)
        (D.field "opponentName" D.string)
        (D.field "appointment" D.string)
        (D.field "comment" D.string)

toGameMode : String -> Decoder GameMode
toGameMode s =
    case s of
       "Duel" -> D.succeed Duel
       "CTF" -> D.succeed CTF
       "TwoVsTwo" -> D.succeed TwoVsTwo
       "WipeOut" -> D.succeed WipeOut
       _ -> D.fail "Couldn't parse GameMode"

fetchPendingChallenges : MsgConstructor (List PendingChallengeEntry) msg -> Cmd msg
fetchPendingChallenges msg =
    Http.get
            { url = "/api/challenge/pending"
            , expect = expectJsonWithErrorHandling pendingChallengesDecoder msg
            }

performAcceptChallenge : String -> MsgConstructor () msg -> Cmd msg
performAcceptChallenge challengeId msg =
    Http.request
            { method = "PUT"
            , headers = []
            , url = "/api/challenge/"++challengeId++"/accept"
            , body = emptyBody
            , expect = expectStringWithErrorHandling msg
            , timeout = Nothing
            , tracker = Nothing
            }

performFetchPendingChallenge: String -> MsgConstructor PendingChallengeEntry msg -> Cmd msg
performFetchPendingChallenge challengeId msg =
    Http.get
        { url = "/api/challenge/pending/"++challengeId
        , expect = expectJsonWithErrorHandling pendingChallengeDecoder msg
        }