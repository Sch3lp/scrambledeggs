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


handleBadStatus metadata body =
    if metadata.statusCode == 401 then
        Err NotAuthorized

    else
        Err (BadRequest body)
