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
import Element as Ui
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import Html
import Html.Events
import Http
import Json.Decode
import Json.Encode
import List
import Url
import Url.Parser as Parser


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
    { users : List User
    , registrationStatus : RegistrationState
    , registerInput : String
    }


emptyModel : Model
emptyModel =
    { users = []
    , registrationStatus = NotRegistered
    , registerInput = ""
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



-- Defining the client side routes for our app


type Route
    = Register
    | Leaderboards
    | ChallengePlayer
    | Report
    | NotFound



-- UPDATE


type Msg
    = NoOp
    | UpdateRegisterInput String
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

        UpdateRegisterInput str ->
            ( { model | registerInput = str }
            , Cmd.none
            )

        GotRegisterPlayerResponse result ->
            handleRegisterPlayerResponse model result



-- GenerateRandomUser ->
--     generateRandomUser model
-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- HTTP


registerPlayer : Model -> ( Model, Cmd Msg )
registerPlayer model =
    let
        playerNameJson =
            model.users
                |> List.head
                |> getUsername
                |> newPlayerName
    in
    ( { model | registrationStatus = CallingAPI }
    , Http.post
        { url = "/api/register"
        , body = Http.jsonBody playerNameJson
        , expect = expectStringWithErrorHandling GotRegisterPlayerResponse
        }
    )



-- This is a constructor function to get a PlayerNameJson object from a string


newPlayerName : String -> Json.Encode.Value
newPlayerName name =
    Json.Encode.object [ ( "name", Json.Encode.string name ) ]


handleRegisterPlayerResponse : Model -> Result ApiError value -> ( Model, Cmd Msg )
handleRegisterPlayerResponse model result =
    case result of
        Ok _ ->
            ( { model
                | users = { username = "test" } :: model.users
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


expectStringWithErrorHandling : (Result ApiError String -> msg) -> Http.Expect msg
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
                    Err (BadRequest body)

                Http.GoodStatus_ metadata body ->
                    Ok body
        )



-- APP ROUTING
-- I don't fucking get this


parseUrl : Url.Url -> Route
parseUrl url =
    case Parser.parse parseRoute url of
        Just route ->
            route

        Nothing ->
            Leaderboards


parseRoute : Parser.Parser (Route -> a) a
parseRoute =
    Parser.oneOf
        [ Parser.map Register Parser.top
        , Parser.map Leaderboards (Parser.s "leaderboards")
        , Parser.map Report (Parser.s "reports")
        ]



-- VIEW


view : Model -> Html.Html Msg
view model =
    Ui.layout
        [ Background.color
            (Ui.rgb 222 230 255)
        ]
        (Ui.column
            [ Ui.height Ui.fill
            , Ui.width Ui.fill
            , Ui.centerX
            , Ui.paddingEach { top = 150, left = 0, right = 0, bottom = 0 }
            , Ui.spacing 16
            , Font.color (Ui.rgb255 41 46 54)

            -- , explain Debug.todo -- Awesome layout debugging
            ]
            [ viewRegisterInput model.registerInput
            , viewRegisterButton (model.registerInput == "")
            , viewStatusMessage model.registrationStatus
            , viewLeaderboards (List.repeat 10 { username = "Henkie" }) -- Eventually pass leaderboards data here
            , infoFooter
            ]
        )


viewLeaderboards : List User -> Ui.Element Msg
viewLeaderboards userList =
    Ui.column []
        (List.map
            (\u -> Ui.row [] [ Ui.text u.username ])
            userList
        )


viewRegisterInput : String -> Ui.Element Msg
viewRegisterInput value =
    Ui.el
        [ Ui.centerX ]
        (Input.text
            [ onEnter RegisterButtonClicked
            ]
            { label = Input.labelLeft [] (Ui.text "Username")
            , onChange = UpdateRegisterInput
            , placeholder = Just (Input.placeholder [] (Ui.text "L33tSn!per69"))
            , text = value
            }
        )



-- The docs basically say not to use disable for accessibility reasons https://package.elm-lang.org/packages/mdgriffith/elm-ui/latest/Element-Input#disabling-inputs
-- This button does nothing when disabled, otherwise sends RegisterButtonClicked msg


viewRegisterButton : Bool -> Ui.Element Msg
viewRegisterButton isDisabled =
    let
        sharedAttributes =
            [ Ui.centerX
            , Border.width 1
            , Border.rounded 8
            , Ui.paddingXY 16 8
            ]
    in
    if isDisabled == True then
        Input.button
            (List.append
                sharedAttributes
                [ Background.color (Ui.rgb255 200 200 200)
                , Font.color (Ui.rgb255 100 100 100)
                ]
            )
            { label = Ui.text "Please enter a username", onPress = Just NoOp }

    else
        Input.button
            (List.append
                sharedAttributes
                [ Background.color (Ui.rgb 150 150 150) ]
            )
            { label = Ui.text "Register", onPress = Just RegisterButtonClicked }


viewStatusMessage : RegistrationState -> Ui.Element Msg
viewStatusMessage registrationStatus =
    let
        sharedAttributes =
            [ Ui.width (Ui.fill |> Ui.maximum 600), Ui.centerX ]
    in
    case registrationStatus of
        NotRegistered ->
            Ui.none

        Registered ->
            Ui.paragraph sharedAttributes
                [ Ui.text "Yay! You are now registered for the Scramble Ladder." ]

        CallingAPI ->
            Ui.paragraph sharedAttributes
                [ Ui.text "Loading" ]

        Failed errorMessage ->
            Ui.paragraph sharedAttributes
                [ Ui.text errorMessage ]


infoFooter : Ui.Element msg
infoFooter =
    Ui.el
        [ Ui.alignBottom
        , Ui.centerX
        , Background.color (Ui.rgb255 51 67 92)
        , Font.color (Ui.rgb 0xEE 0xEE 0xEE) -- Hex color example
        , Ui.width Ui.fill
        , Ui.paddingXY 16 32
        , Font.size 14
        ]
        (Ui.text "Diabotical District -- Where passionate trashy nerds align their goals")



-- Helper functions


onEnter : msg -> Ui.Attribute msg
onEnter msg =
    Ui.htmlAttribute
        (Html.Events.on "keyup"
            (Json.Decode.field "key" Json.Decode.string
                |> Json.Decode.andThen
                    (\key ->
                        if key == "Enter" then
                            Json.Decode.succeed msg

                        else
                            Json.Decode.fail "Not the enter key"
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
