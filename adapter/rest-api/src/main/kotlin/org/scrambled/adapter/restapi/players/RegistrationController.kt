package org.scrambled.adapter.restapi.players

import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping(
    "/api/register",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class RegistrationController(
    private val commandExecutor: CommandExecutor,
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerPlayer(
        @RequestBody(required = true) registrationInfo: RegisterPlayerJson,
        builder: UriComponentsBuilder
    ): ResponseEntity<Any> {
        // TODO extract sub + provider from authenticated JWT token (because I'm not sure if providers guarantee universally unique identifiers)
        // otherwise if someone logs in with Facebook, and gets sub 1234
        // and somebody completely different logs in with Google, and also gets sub 1234
        // both of these people (who are physically different people, aka different players) will be able to log in to each others account

        val registerPlayer = RegisterPlayer(nickname = registrationInfo.nickname)
        val registeredPlayer = commandExecutor.execute(registerPlayer)

        val locationUri = builder.path("/api/player/{id}").buildAndExpand(registeredPlayer.id).toUri()

        return ResponseEntity.created(locationUri).build()
    }
}

data class RegisterPlayerJson(val nickname: String)
