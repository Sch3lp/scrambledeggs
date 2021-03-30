module MainRoutingTest exposing (..)

import Expect
import Test exposing (Test, describe, test)


suite : Test
suite =
    describe "A Snarf"
        [ test "can haz cheezburger" <|
            \_ ->
                let
                    snarf =
                        "Snarf"

                    actual =
                        canHaz snarf
                in
                Expect.equal "Snarf can haz cheezburger" actual
        ]


canHaz name =
    name ++ " can haz cheezburger"
