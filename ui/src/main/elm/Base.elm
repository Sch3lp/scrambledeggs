module Base exposing (ButtonProps, button, contrastedPalette, leaderboardTable, onEnter, palette)

import Element as Ui
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input
import Html.Events
import Json.Decode as D
import String exposing (fromInt)
import Url.Builder as UrlBuilder


type alias ButtonProps =
    { isDisabled : Bool, label : String }


button : ButtonProps -> msg -> Ui.Element msg
button { isDisabled, label } msg =
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
            ([ Background.color palette.bgDisabled
             , Font.color palette.fontDisabled
             ]
                ++ sharedAttributes
            )
            { label = Ui.text label, onPress = Nothing }

    else
        Input.button
            ([ Background.color palette.primary
             , Font.color palette.font
             ]
                ++ sharedAttributes
            )
            { label = Ui.text label, onPress = Just msg }



-- TODO: maybe move to its own module at some point


leaderboardTablePadding =
    { top = 0, bottom = 0, left = 10, right = 0 }


leaderboardTable : Palette -> List { a | rank : Maybe Int, nickname : String, playerId : String } -> Ui.Element msg
leaderboardTable givenPalette leaderboard =
    Ui.table []
        { data = leaderboard
        , columns =
            [ { header = columnHeader givenPalette "Rank"
              , width = Ui.shrink
              , view =
                    \entry ->
                        Ui.el [ Ui.paddingEach leaderboardTablePadding ] <| Ui.text <| rankAsString entry.rank
              }
            , { header = columnHeader givenPalette "Nickname"
              , width = Ui.fill
              , view =
                    \entry ->
                        entryAsLink givenPalette entry
              }
            ]
        }

--TODO: Ui.link generates a simple href, but maybe we want to use Nav.pushUrl,
-- eventhough that requires a Nav.key which we need to pass all the way down ?
entryAsLink givenPalette entry =
    Ui.link [ Ui.paddingEach leaderboardTablePadding ] <|
        { url = UrlBuilder.relative [ "challenge", entry.playerId ] []
        , label = Ui.el [Font.color givenPalette.href ] <| Ui.text entry.nickname
        }


columnHeader givenPalette title =
    Ui.el
        [ Font.color givenPalette.highlight
        , Font.size 18
        , Ui.paddingEach leaderboardTablePadding
        ]
        (Ui.text title)


rankAsString rank =
    Maybe.withDefault "" <| Maybe.map (\i -> i |> fromInt |> String.append "#") rank



-- View helper functions


onEnter : msg -> Ui.Attribute msg
onEnter msg =
    Ui.htmlAttribute
        (Html.Events.on "keyup"
            (D.field "key" D.string
                |> D.andThen
                    (\key ->
                        if key == "Enter" then
                            D.succeed msg

                        else
                            D.fail "Not the enter key"
                    )
            )
        )


type alias Palette =
    { font : Ui.Color
    , fontDisabled : Ui.Color
    , bg : Ui.Color
    , bgDisabled : Ui.Color
    , primary : Ui.Color
    , error : Ui.Color
    , highlight : Ui.Color
    , href : Ui.Color
    }


palette : Palette
palette =
    { font = Ui.rgb255 41 46 54
    , fontDisabled = Ui.rgb255 100 100 100
    , bg = Ui.rgb255 222 230 255
    , bgDisabled = Ui.rgb255 200 200 200
    , primary = Ui.rgb255 150 150 150
    , error = Ui.rgb255 178 15 15
    , highlight = Ui.rgb255 64 77 145
    , href = Ui.rgb255 0 83 91
    }


contrastedPalette =
    contrast palette


contrast : Palette -> Palette
contrast p =
    { p
        | font = Ui.rgb255 238 238 238
        , fontDisabled = Ui.rgb255 222 230 255
        , bg = Ui.rgb255 51 67 92
        , bgDisabled = Ui.rgb255 51 67 92
        , primary = Ui.rgb255 150 150 150
        , error = Ui.rgb255 178 15 15
    }
