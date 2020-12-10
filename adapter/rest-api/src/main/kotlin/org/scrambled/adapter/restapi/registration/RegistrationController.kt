package org.scrambled.adapter.restapi.registration

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/register",
    produces = [MediaType.APPLICATION_JSON_VALUE])
class RegistrationController {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerPlayer(@RequestBody(required = true) playerName: PlayerNameJson): ResponseEntity<PlayerNameJson> {
        // TODO extract sub + provider from authenticated JWT token (because I'm not sure if providers guarantee universally unique identifiers)
        // otherwise if someone logs in with Facebook, and gets sub 1234
        // and somebody completely different logs in with Google, and also gets sub 1234
        // both of these people (who are physically different people, aka different players) will be able to log in to each others account
        val sub = "1234"

        return ResponseEntity.ok(playerName)
    }
}

data class PlayerNameJson(val name: String)