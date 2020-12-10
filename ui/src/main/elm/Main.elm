module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

  1. Model  - a full definition of the application's state
  2. Update - a way to step the application state forward
  3. View   - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>
-}

import Browser
import Html exposing (..)
import Html.Attributes exposing (..)
import Html.Events exposing (..)
import Html.Lazy exposing (lazy)
import Http
import Json.Decode as Json


main : Program (Maybe String) Model Msg
main =
    Browser.document
        { init = init
        , view = \model -> { title = "Scramble • Diabotical Ladder", body = [view model] }
        , update = update
        , subscriptions = subscriptions
        }



-- MODEL

type ApiPost
    = Failure
    | Loading

-- The full application state of our todo app.
type alias Model =
    { nicknameField : String
    , successMessage : String
    , postRegisterPlayer: Maybe ApiPost
    }


emptyModel : Model
emptyModel =
    { nicknameField = ""
    , successMessage = ""
    , postRegisterPlayer = Nothing
    }


init : (Maybe String) -> ( Model, Cmd Msg )
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
    | GotText (Result Http.Error String)



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

        GotText result ->
            handleRegisterPlayer model result

-- SUBSCRIPTIONS

subscriptions : Model -> Sub Msg
subscriptions _ =
  Sub.none


-- HTTP

registerPlayer: Model -> ( Model, Cmd Msg)
registerPlayer model =
    ( { model | postRegisterPlayer = Just Loading }
      , Http.post
          { url = "/api/register"
          , body = String.concat ["{\"name\": \"",model.nicknameField,"\"}"]
                |> Http.stringBody "application/json"
          , expect = Http.expectString GotText
          }
      )


handleRegisterPlayer: Model -> Result error value -> ( Model, Cmd Msg)
handleRegisterPlayer model result =
    case result of
        Ok _ ->
          ( { model | nicknameField = ""
            , successMessage = "Yay! You are now registered for the Scramble Ladder."
            } , Cmd.none )
        Err _ ->
          (   { model | postRegisterPlayer = Just Failure }, Cmd.none )



-- VIEW


view : Model -> Html Msg
view model =
    div
        [ class "todomvc-wrapper"
        , style "visibility" "hidden"
        ]
        [ section
            [ class "todoapp" ]
            [ lazy viewInput model.nicknameField
            , lazy viewRegisterButton model.successMessage
            , lazy viewSuccessMessage model.successMessage
            ]
        , infoFooter
        ]

viewRegisterButton message =
    let
        isDisabled =
                    if String.isEmpty message then
                        disabled False
                    else
                        disabled True
    in
        button [ isDisabled, onClick RegisterButtonClicked ] [ text "Register" ]

viewSuccessMessage : String -> Html Msg
viewSuccessMessage message =
    let
        cssVisibility =
                    if String.isEmpty message then
                        "hidden"
                    else
                        "visible"
    in
        p [style "visibility" cssVisibility] [ text message ]

viewInput : String -> Html Msg
viewInput task =
    header
        [ class "header" ]
        [ h1 [] [ text "Register for Scrambled!" ]
        , input
            [ class "new-todo"
            , placeholder "Your nickname (you use in Diabotical)"
            , autofocus True
            , value task
            , name "nickname"
            , onInput UpdateNicknameField
            , onEnter RegisterButtonClicked
            ]
            []
        ]


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
        [ p [] [ text "Click the button to be awesome" ]
        ]