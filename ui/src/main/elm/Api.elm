module Api exposing (..)

import Http
import Json.Decode as D exposing (Decoder)


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
                    handleBadStatus metadata body

                Http.GoodStatus_ metadata body ->
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
