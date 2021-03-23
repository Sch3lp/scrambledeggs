module Main exposing (..)

{-| TodoMVC implemented in Elm, using plain HTML and CSS for rendering.

This application is broken up into three key parts:

1.  Model - a full definition of the application's state
2.  Update - a way to step the application state forward
3.  View - a way to visualize our application state with HTML

This clean division of concerns is a core part of Elm. You can read more about
this in <http://guide.elm-lang.org/architecture/index.html>

-}

import Api exposing (ApiError(..))
import Base
import Browser
import Element as Ui
import Element.Background as Background
import Element.Font as Font
import Home exposing (viewHome)
import Html
import List
import Registration exposing (RegisteredPlayer, RegistrationState(..))
import Url
import Url.Parser as Parser
import Widget.Material.Typography as Typo



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
    , Cmd.map HomeMsg Home.performFetchLeaderboard
    )



-- MODEL
-- The full application state of our app.


type alias Model =
    { homeModel : Home.Model
    , registrationModel : Registration.Model
    }


setRegistrationModel : Registration.Model -> Model -> Model
setRegistrationModel newRegModel model =
    { model | registrationModel = newRegModel }


setHomeModel : Home.Model -> Model -> Model
setHomeModel newHomeModel model =
    { model | homeModel = newHomeModel }


emptyModel =
    { homeModel = Home.emptyModel
    , registrationModel = Registration.emptyModel
    }



-- UPDATE


type Msg
    = RegistrationMsg Registration.Msg
    | HomeMsg Home.Msg


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RegistrationMsg m ->
            let
                ( newRegistrationModel, newRegistrationMsg ) =
                    Registration.update m model.registrationModel
            in
            ( setRegistrationModel newRegistrationModel model
            , Cmd.map RegistrationMsg newRegistrationMsg
            )

        HomeMsg m ->
            let
                ( newHomeModel, newHomeMsg ) =
                    Home.update m model.homeModel
            in
            ( setHomeModel newHomeModel model
            , Cmd.map HomeMsg newHomeMsg
            )



-- SUBSCRIPTIONS


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



-- Todo APP ROUTING


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
    --viewRegistration model.registrationModel
    --    |> List.map (Ui.map RegistrationMsg)
    viewHome model.homeModel
        |> List.map (Ui.map HomeMsg)



-- TODO
-- * [x] Extract button helper function so that our buttons will always look the same
-- * [x] Split up Main.elm into a registration page and an anonymous home page
-- * [ ] Add routing based on Url
-- * [ ] Fetch both the registeredPlayers and the leaderboard at the same time; look at Task thing in Elm again
-- * [ ] Replace our own palette with that of Material somehow
-- * [ ] Extract the API stuff (fetching players, fetching leaderboard, registering new player) into its own module
-- * [ ] Splitting Api calls from a component/module is interesting if we can unit test the model without having to worry about actually performing http
