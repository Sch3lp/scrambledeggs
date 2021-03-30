module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Base
import Browser exposing (UrlRequest)
import Browser.Navigation as Nav exposing (Key, pushUrl)
import Element as Ui
import Element.Background as Background
import Element.Font as Font
import Home
import Html
import List
import Registration
import Url exposing (Url)
import Url.Parser as Parser
import Widget.Material.Typography as Typo



-- Main function
-- App init


main : Program (Maybe String) Model Msg
main =
    Browser.application
        { init = init
        , view =
            \model ->
                { title = "Scramble • Diabotical Ladder", body = [ view model ] }
        , update = update
        , subscriptions = subscriptions
        , onUrlRequest = onUrlRequest
        , onUrlChange = onUrlChange
        }


onUrlRequest : UrlRequest -> Msg
onUrlRequest =
    LinkClicked


onUrlChange : Url -> Msg
onUrlChange url =
    UrlChanged url


type Route
    = AnonymousHomepage
    | Registration


parseUrl : Url.Url -> Route
parseUrl url =
    case Parser.parse parseRoute url of
        Just route ->
            route

        Nothing ->
            AnonymousHomepage


parseRoute : Parser.Parser (Route -> a) a
parseRoute =
    Parser.oneOf
        [ Parser.map AnonymousHomepage Parser.top
        , Parser.map Registration (Parser.s "register")
        ]


init : Maybe String -> Url -> Key -> ( Model, Cmd Msg )
init s url key =
    ( emptyModel key url
    , Cmd.map HomeMsg Home.performFetchLeaderboard
    )



-- MODEL
-- The full application state of our app.


type alias Model =
    { homeModel : Home.Model
    , registrationModel : Registration.Model
    , currentRoute : Route
    , key : Key
    , url : Url
    }


setRegistrationModel : Registration.Model -> Model -> Model
setRegistrationModel newRegModel model =
    { model | registrationModel = newRegModel }


setHomeModel : Home.Model -> Model -> Model
setHomeModel newHomeModel model =
    { model | homeModel = newHomeModel }


emptyModel : Nav.Key -> Url -> Model
emptyModel key url =
    Model (Home.emptyModel key) (Registration.emptyModel key) AnonymousHomepage key url



-- UPDATE


type Msg
    = RegistrationMsg Registration.Msg
    | HomeMsg Home.Msg
    | UrlChanged Url
    | LinkClicked UrlRequest


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RegistrationMsg subMsg ->
            let
                ( newRegistrationModel, newRegistrationMsg ) =
                    Registration.update subMsg model.registrationModel
            in
            ( setRegistrationModel newRegistrationModel model
            , Cmd.map RegistrationMsg newRegistrationMsg
            )

        HomeMsg subMsg ->
            let
                ( newHomeModel, newHomeMsg ) =
                    Home.update subMsg model.homeModel
            in
            ( setHomeModel newHomeModel model
            , Cmd.map HomeMsg newHomeMsg
            )

        UrlChanged url ->
            ( { model | currentRoute = parseUrl url }, Cmd.none )

        LinkClicked urlRequest ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Nav.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Nav.load href )



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- VIEW


view : Model -> Html.Html Msg
view model =
    Ui.layout
        [ Background.color Base.palette.bg
        ]
        (Ui.column
            [ Ui.height Ui.fill
            , Ui.width Ui.fill
            , Ui.centerX
            , Ui.alignTop
            , Font.color Base.palette.font
            ]
         <|
            viewHeader model
                ++ viewMainContent model
                ++ viewFooter model
        )


viewHeader model =
    [ Ui.el
        ([ Ui.alignTop
         , Ui.centerX
         , Ui.centerY
         , Ui.width Ui.fill
         , Background.color <| Base.contrastedPalette.bg
         , Font.color <| Base.contrastedPalette.font
         , Ui.padding 25
         ]
            ++ Typo.h1
        )
        header
    ]


header =
    Ui.text "Scramblede.gg"


viewFooter model =
    [ Ui.el
        [ Ui.alignBottom
        , Ui.centerX
        , Ui.width Ui.fill
        , Background.color <| Base.contrastedPalette.bg
        , Font.color <| Base.contrastedPalette.font
        , Font.size 14
        , Ui.paddingXY 16 32
        ]
        footer
    ]


footer : Ui.Element msg
footer =
    Ui.text "Diabotical District -- Where passionate trashy nerds align their goals"


viewMainContent : Model -> List (Ui.Element Msg)
viewMainContent model =
    -- Todo: implement routing (based on url)
    let
        viewRoute =
            if isRegistrationActive model then
                Registration.viewRegistration model.registrationModel
                    |> List.map (Ui.map RegistrationMsg)

            else
                Home.viewHome model.homeModel
                    |> List.map (Ui.map HomeMsg)
    in
    viewRoute


isRegistrationActive model =
    model.currentRoute == Registration



-- TODO
-- * [x] Extract button helper function so that our buttons will always look the same
-- * [x] Split up Main.elm into a registration page and an anonymous home page
-- * [ ] Add routing based on Url
-- * [ ] Fetch both the recentMatches and the leaderboard at the same time; look at Task thing in Elm again
-- * [ ] Replace our own palette with that of Material somehow
-- * [ ] Extract the API stuff (fetching players, fetching leaderboard, registering new player) into its own module
-- * [ ] Splitting Api calls from a component/module is interesting if we can unit test the model without having to worry about actually performing http
