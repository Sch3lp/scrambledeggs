module Base exposing (ButtonProps, button)

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
            (List.append
                sharedAttributes
                [ Background.color (Ui.rgb255 200 200 200)
                , Font.color (Ui.rgb255 100 100 100)
                ]
            )
            { label = Ui.text label, onPress = Nothing }

    else
        Input.button
            (List.append
                sharedAttributes
                [ Background.color (Ui.rgb 150 150 150) ]
            )
            { label = Ui.text label, onPress = Just msg }
