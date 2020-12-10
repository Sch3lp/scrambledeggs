port module Main exposing (..)

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
import Json.Decode as Json


main : Program (Maybe Model) Model Msg
main =
    Browser.document
        { init = init
        , view = \model -> { title = "Elm • TodoMVC", body = [view model] }
        , update = updateWithStorage
        , subscriptions = \_ -> Sub.none
        }


port setStorage : Model -> Cmd msg


{-| We want to `setStorage` on every update. This function adds the setStorage
command for every step of the update function.
-}
updateWithStorage : Msg -> Model -> ( Model, Cmd Msg )
updateWithStorage msg model =
    let
        ( newModel, cmds ) =
            update msg model
    in
        ( newModel
        , Cmd.batch [ setStorage newModel, cmds ]
        )



-- MODEL


-- The full application state of our todo app.
type alias Model =
    { nicknameField : String
    , successMessage : String
    }


emptyModel : Model
emptyModel =
    { nicknameField = ""
    , successMessage = ""
    }


init : Maybe Model -> ( Model, Cmd Msg )
init maybeModel =
  ( Maybe.withDefault emptyModel maybeModel
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



-- How we update our Model on a given Msg?
update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NoOp ->
            ( model, Cmd.none )

        RegisterButtonClicked ->
            ( { model
                | nicknameField = ""
                , successMessage = "Yay! You are now registered for the Scramble Ladder."
              }
            , Cmd.none
            )

        UpdateNicknameField str ->
            ( { model | nicknameField = str }
            , Cmd.none
            )


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
        button [ isDisabled ] [ text "Register" ]

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