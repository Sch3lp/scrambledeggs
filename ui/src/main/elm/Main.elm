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



-- Main function
-- App init


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


init : Maybe String -> ( Model, Cmd Msg )
init _ =
    ( emptyModel
    , Cmd.none
    )



-- MODEL
-- The full application state of our app.


type alias Model =
    { users : List User
    , registrationStatus : RegistrationState
    , registerInput : String
    }


emptyModel : Model
emptyModel =
    { users =
        -- Prefill for testing
        [ { username = "Henkie" }
        , { username = "Bertie" }
        , { username = "Jos" }
        ]
    , registrationStatus = NotRegistered
    , registerInput = ""
    }



-- Types


type RegistrationState
    = NotRegistered
    | Registered
    | Failed String
    | CallingAPI


type alias User =
    { username : String

    -- Other stuff can go here, such as email, avatar
    }



-- UPDATE


type Msg
    = NoOp
    | UpdateRegisterInput String
    | RegisterButtonClicked
    | GotUser (Result ApiError String)


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

        GotUser result ->
            handleRegisterPlayerResponse model result



-- HTTP requests & helper functions


type ApiError
    = BadRequest String
    | NetworkError
    | Timeout
    | BadUrl String


registerPlayer : Model -> ( Model, Cmd Msg )
registerPlayer model =
    let
        playerNameJson =
            model.registerInput
                |> userEncoder
    in
    ( { model | registrationStatus = CallingAPI }
    , Http.post
        { url = "/api/register"
        , body = Http.jsonBody playerNameJson
        , expect = expectStringWithErrorHandling GotUser
        }
    )


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


handleRegisterPlayerResponse : Model -> Result ApiError String -> ( Model, Cmd Msg )
handleRegisterPlayerResponse model result =
    case result of
        Ok userStr ->
            ( { model
                | users = { username = userStr } :: model.users -- push user onto list
                , registrationStatus = Registered
              }
            , Cmd.none
            )

        Err err ->
            case err of
                BadRequest str ->
                    ( { model | registrationStatus = Failed str }, Cmd.none )

                NetworkError ->
                    ( { model | registrationStatus = Failed "Network Error" }, Cmd.none )

                Timeout ->
                    ( { model | registrationStatus = Failed "Timeout" }, Cmd.none )

                BadUrl a ->
                    ( { model | registrationStatus = Failed a }, Cmd.none )



--    -- When it's a BadRequest, we care about the response, because it contains an insightful error message.
--         Err (BadRequest errorMsg) ->
--             ( { model | registrationStatus = Failed errorMsg }, Cmd.none )
--         -- When it's any other ApiError we don't care about specifics.
--         _ ->
--             ( { model | registrationStatus = Failed "Something went wrong." }, Cmd.none )


userDecoder : Json.Decode.Decoder User
userDecoder =
    Json.Decode.map User (Json.Decode.field "username" Json.Decode.string)


userEncoder : String -> Json.Encode.Value
userEncoder name =
    Json.Encode.object [ ( "username", Json.Encode.string name ) ]


httpErrorToString : Http.Error -> String
httpErrorToString error =
    case error of
        Http.BadUrl url ->
            "The URL " ++ url ++ " was invalid"

        Http.Timeout ->
            "Unable to reach the server, try again"

        Http.NetworkError ->
            "Unable to reach the server, check your network connection"

        Http.BadStatus 500 ->
            "The server had a problem, try again later"

        Http.BadStatus 400 ->
            "Verify your information and try again"

        Http.BadStatus x ->
            "Unknown error: " ++ String.fromInt x

        Http.BadBody errorMessage ->
            errorMessage



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- APP ROUTING
-- I don't fucking get this
-- Does nothing atm


type Route
    = Register
    | Leaderboards
    | ChallengePlayer
    | Report
    | NotFound


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
            , viewLeaderboards model.users
            , infoFooter
            ]
        )


viewLeaderboards : List User -> Ui.Element Msg
viewLeaderboards userList =
    Ui.column [ Ui.centerX ]
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
            [ Ui.width (Ui.fill |> Ui.maximum 600), Ui.centerX, Font.center ]
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



-- View helper functions


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
