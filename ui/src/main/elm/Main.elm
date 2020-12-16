module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Browser
import Element exposing (Element, column, el, fill, layout, rgb, rgb255, rgba, row, text)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import Html exposing (Attribute, Html, div, footer, h1, header, input, p, section)
import Html.Attributes exposing (..)
import Html.Events exposing (..)
import Html.Lazy exposing (lazy)
import Http exposing (Error(..), Expect, expectStringResponse)
import Json.Decode as Json
import Json.Encode as Encode


main : Program (Maybe String) Model Msg
main =
    Browser.document
        { init = init
        , view =
            \model ->
                { title = "Scramble • Diabotical Ladder", body = [ view model ] }
        , update = update
        , subscriptions = subscriptions
        }



-- MODEL


type ApiPost
    = Failure String
    | Loading



-- The full application state of our todo app.


type alias Model =
    { nicknameField : String
    , successMessage : String
    , postRegisterPlayer : Maybe ApiPost
    }


emptyModel : Model
emptyModel =
    { nicknameField = ""
    , successMessage = ""
    , postRegisterPlayer = Nothing
    }


init : Maybe String -> ( Model, Cmd Msg )
init _ =
    ( emptyModel
    , Cmd.none
    )



-- UPDATE


{-| Users of our app can trigger messages by clicking and typing. These
messages are fed into the `update` function as they occur, letting us react
to them.
-}
type Msg
    = NoOp
    | UpdateNicknameField String
    | RegisterButtonClicked
    | GotRegisterPlayerResponse (Result ApiError String)



-- How we update our Model on a given Msg?


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        RegisterButtonClicked ->
            registerPlayer model

        UpdateNicknameField str ->
            ( { model | nicknameField = str }
            , Cmd.none
            )

        GotRegisterPlayerResponse result ->
            handleRegisterPlayerResponse model result



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- HTTP


registerPlayer : Model -> ( Model, Cmd Msg )
registerPlayer model =
    let
        playerNameJson =
            newPlayerName model.nicknameField
    in
    ( { model | postRegisterPlayer = Just Loading }
    , Http.post
        { url = "/api/register"
        , body = Http.jsonBody playerNameJson
        , expect = expectStringWithErrorHandling GotRegisterPlayerResponse
        }
    )



-- This is a constructor function to get a PlayerNameJson object from a string


newPlayerName : String -> Encode.Value
newPlayerName name =
    Encode.object [ ( "name", Encode.string name ) ]


handleRegisterPlayerResponse : Model -> Result ApiError value -> ( Model, Cmd Msg )
handleRegisterPlayerResponse model result =
    case result of
        Ok _ ->
            ( { model
                | nicknameField = ""
                , successMessage = "Yay! You are now registered for the Scramble Ladder."
              }
            , Cmd.none
            )

        -- When it's a BadRequest, we care about the response, because it contains an insightful error message.
        Err (BadRequest msg) ->
            ( { model | postRegisterPlayer = Just (Failure msg) }, Cmd.none )

        -- When it's any other ApiError we don't care about specifics.
        _ ->
            ( { model | postRegisterPlayer = Just (Failure "Something went wrong.") }, Cmd.none )



-- When we need to decode, replace String with "a" and provide a Decoder function as 2nd argument to transform "a"


expectStringWithErrorHandling : (Result ApiError String -> msg) -> Expect msg
expectStringWithErrorHandling toMsg =
    expectStringResponse toMsg <|
        \response ->
            case response of
                Http.BadUrl_ url ->
                    Err (BadUrl url)

                Http.Timeout_ ->
                    Err Timeout

                Http.NetworkError_ ->
                    Err NetworkError

                Http.BadStatus_ metadata body ->
                    Err (BadRequest body)

                Http.GoodStatus_ metadata body ->
                    Ok body


type ApiError
    = BadRequest String
    | NetworkError
    | Timeout
    | BadUrl String



-- VIEW


view : Model -> Html Msg
view model =
    -- I don't understand why we have to pipe
    layout
        []
    <|
        column
            [ Background.color (rgb255 92 99 118) ]
            [ el [] (text "HI")
            , viewInput model.nicknameField
            ]



-- [ section
--     [ class "todoapp" ]
--     [ lazy viewInput model.nicknameField
--     , lazy viewRegisterButton model.successMessage
--     , lazy viewSuccessMessage model.successMessage
--     , lazy viewFailureMessage model.postRegisterPlayer
--     ]
-- , infoFooter
-- ]


viewRegisterButton : String -> Html Msg
viewRegisterButton message =
    let
        isDisabled =
            if String.isEmpty message then
                disabled False

            else
                disabled True
    in
    Html.button [ isDisabled, onClick RegisterButtonClicked ] [ Html.text "Register" ]


viewSuccessMessage : String -> Html Msg
viewSuccessMessage message =
    let
        cssVisibility =
            if String.isEmpty message then
                "hidden"

            else
                "visible"
    in
    p [ style "visibility" cssVisibility ] [ Html.text message ]


viewFailureMessage : Maybe ApiPost -> Html Msg
viewFailureMessage apiPostResult =
    let
        ( cssVisibility, message ) =
            case apiPostResult of
                Just (Failure msg) ->
                    ( "visible", msg )

                _ ->
                    ( "hidden", "" )
    in
    p [ style "visibility" cssVisibility ] [ Html.text message ]


viewInput : String -> Element Msg
viewInput value =
    column
        []
        [ el [] (text "Register for Scrambled!")
        , Input.text
            []
            { label = Input.labelLeft [] (text "Username")
            , onChange = UpdateNicknameField
            , placeholder = Just (Input.placeholder [] (text "Username"))
            , text = value
            }
        ]



-- old
-- header
--     [ class "header" ]
--     [ h1 [] [ text "Register for Scrambled!" ]
--     , input
--         [ class "new-todo"
--         , placeholder "Your nickname (you use in Diabotical)"
--         , autofocus True
--         , value task
--         , name "nickname"
--         , onInput UpdateNicknameField
--         , onEnter RegisterButtonClicked
--         ]
--         []


onEnter : Msg -> Attribute Msg
onEnter msg =
    let
        isEnter code =
            if code == 13 then
                Json.succeed msg

            else
                Json.fail "not ENTER"
    in
    on "keydown" (Json.andThen isEnter keyCode)


infoFooter : Html msg
infoFooter =
    footer [ class "info" ]
        [ p [] [ Html.text "Click the button to be awesome" ]
        ]
