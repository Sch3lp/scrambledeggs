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
import Element exposing (Element, alignBottom, centerX, centerY, column, el, explain, fill, height, layout, maximum, none, padding, paddingEach, paddingXY, paragraph, pointer, px, rgb, rgb255, rgba, row, shrink, spacing, text, width)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font exposing (center)
import Element.Input as Input
import Html exposing (Html, div, footer, h1, header, input, p, section)
import Html.Events exposing (..)
import Html.Lazy exposing (lazy)
import Http exposing (Error(..), Expect, expectStringResponse)
import Json.Decode as Decode
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
-- The full application state of our todo app.


type alias Model =
    { user : Maybe User
    , registrationStatus : RegistrationState
    }


emptyModel : Model
emptyModel =
    { user = Nothing
    , registrationStatus = NotRegistered
    }


init : Maybe String -> ( Model, Cmd Msg )
init _ =
    ( emptyModel
    , Cmd.none
    )


type RegistrationState
    = NotRegistered
    | Registered
    | Failed String
    | CallingAPI


type alias User =
    { username : String

    -- Other stuff can go here, such as email, avatar
    }


type ApiError
    = BadRequest String
    | NetworkError
    | Timeout
    | BadUrl String



-- UPDATE


type Msg
    = NoOp
    | UpdateUsername String
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

        UpdateUsername str ->
            ( { model | user = Just { username = str } }
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
            newPlayerName (getUsername model.user)
    in
    ( { model | registrationStatus = CallingAPI }
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
                | user = Nothing
                , registrationStatus = Registered
              }
            , Cmd.none
            )

        -- When it's a BadRequest, we care about the response, because it contains an insightful error message.
        Err (BadRequest errorMsg) ->
            ( { model | registrationStatus = Failed errorMsg }, Cmd.none )

        -- When it's any other ApiError we don't care about specifics.
        _ ->
            ( { model | registrationStatus = Failed "Something went wrong." }, Cmd.none )



-- When we need to decode, replace String with "a" and provide a Decoder function as 2nd argument to transform "a"


expectStringWithErrorHandling : (Result ApiError String -> msg) -> Expect msg
expectStringWithErrorHandling toMsg =
    expectStringResponse toMsg
        (\response ->
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
        )



-- VIEW


view : Model -> Html Msg
view model =
    layout
        [ Background.color
            (rgb 222 230 255)
        ]
        (column
            [ height fill
            , width fill
            , centerX
            , paddingEach { top = 150, left = 0, right = 0, bottom = 0 }
            , spacing 16
            , Font.color (rgb255 41 46 54)

            -- , explain Debug.todo -- Awesome layout debugging
            ]
            [ viewInput (getUsername model.user)
            , viewRegisterButton (model.user == Nothing || getUsername model.user == "") -- Not ideal? Probably it will change as we go on
            , viewStatusMessage model.registrationStatus
            , infoFooter
            ]
        )


viewInput : String -> Element Msg
viewInput value =
    el
        [ centerX ]
        (Input.text
            [ onEnter RegisterButtonClicked
            ]
            { label = Input.labelLeft [] (text "Username")
            , onChange = UpdateUsername
            , placeholder = Just (Input.placeholder [] (text "L33tSn!per69"))
            , text = value
            }
        )



-- The docs basically say not to use disable for accessibility reasons https://package.elm-lang.org/packages/mdgriffith/elm-ui/latest/Element-Input#disabling-inputs
-- This button does nothing when disabled, otherwise sends RegisterButtonClicked msg


viewRegisterButton : Bool -> Element Msg
viewRegisterButton isDisabled =
    let
        sharedAttributes =
            [ centerX
            , Border.width 1
            , Border.rounded 8
            , paddingXY 16 8
            ]
    in
    if isDisabled == True then
        Input.button
            (List.append
                sharedAttributes
                [ Background.color (rgb255 200 200 200)
                , Font.color (rgb255 100 100 100)
                ]
            )
            { label = text "Please enter a username", onPress = Just NoOp }

    else
        Input.button
            (List.append
                sharedAttributes
                [ Background.color (rgb 150 150 150) ]
            )
            { label = text "Register", onPress = Just RegisterButtonClicked }


viewStatusMessage : RegistrationState -> Element Msg
viewStatusMessage registrationStatus =
    let
        sharedAttributes =
            [ width (fill |> maximum 600), centerX ]
    in
    case registrationStatus of
        NotRegistered ->
            none

        Registered ->
            paragraph sharedAttributes
                [ text "Yay! You are now registered for the Scramble Ladder." ]

        CallingAPI ->
            paragraph sharedAttributes
                [ text "Loading" ]

        Failed errorMessage ->
            paragraph sharedAttributes
                [ text errorMessage ]


infoFooter : Element msg
infoFooter =
    el
        [ alignBottom
        , centerX
        , Background.color (rgb255 51 67 92)
        , Font.color (rgb 0.95 0.95 0.95)
        , width fill
        , paddingXY 16 32
        , Font.size 14
        ]
        (text "Diabotical District -- Where passionate trashy nerds align their goals")



-- Helper functions


onEnter : msg -> Element.Attribute msg
onEnter msg =
    Element.htmlAttribute
        (Html.Events.on "keyup"
            (Decode.field "key" Decode.string
                |> Decode.andThen
                    (\key ->
                        if key == "Enter" then
                            Decode.succeed msg

                        else
                            Decode.fail "Not the enter key"
                    )
            )
        )


getUsername : Maybe User -> String
getUsername user =
    case user of
        Just a ->
            a.username

        Nothing ->
            ""
