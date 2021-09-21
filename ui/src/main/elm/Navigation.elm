module Navigation exposing(redirectToHome)

import Browser.Navigation as Nav
import Url.Builder as UrlBuilder



redirectToHome model = 
    (model, Nav.pushUrl model.key <| UrlBuilder.relative ["/"] [])