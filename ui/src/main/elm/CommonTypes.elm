module CommonTypes exposing (..)

type GameMode
    = Duel
    | TwoVsTwo
    | WipeOut
    | CTF

gameModeAsString mode =
    case mode of
        Duel ->
            "Duel"

        TwoVsTwo ->
            "TwoVsTwo"

        WipeOut ->
            "WipeOut"

        CTF ->
            "CTF"
