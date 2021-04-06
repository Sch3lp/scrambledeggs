package org.scrambled.adapter.restapi.players

import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
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
        // TODO extract sub + iss from authenticated JWT token
        val registerPlayer = RegisterPlayer(nickname = registrationInfo.nickname, externalAccountRef = ExternalAccountRef(registrationInfo.jwtIss, registrationInfo.jwtSub))
        val registeredPlayer = commandExecutor.execute(registerPlayer)

        val locationUri = builder.path("/api/player/{id}").buildAndExpand(registeredPlayer.id).toUri()

        return ResponseEntity.created(locationUri).build()
    }
}

data class RegisterPlayerJson(val nickname: String, val jwtIss: JwtIss, val jwtSub: JwtSub)
