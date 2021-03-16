module Base exposing (ButtonProps, button, contrastedPalette, palette)

import Element as Ui
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Element.Input as Input


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


type alias Palette =
    { font : Ui.Color
    , fontDisabled : Ui.Color
    , bg : Ui.Color
    , bgDisabled : Ui.Color
    , primary : Ui.Color
    }


palette : Palette
palette =
    { font = Ui.rgb255 41 46 54
    , fontDisabled = Ui.rgb255 100 100 100
    , bg = Ui.rgb 222 230 255
    , bgDisabled = Ui.rgb255 200 200 200
    , primary = Ui.rgb 150 150 150
    }


contrastedPalette =
    contrast palette


contrast : Palette -> Palette
contrast _ =
    { font = Ui.rgb 238 238 238
    , fontDisabled = Ui.rgb 222 230 255
    , bg = Ui.rgb255 51 67 92
    , bgDisabled = Ui.rgb255 51 67 92
    , primary = Ui.rgb 150 150 150
    }
