package org.scrambled.adapter.restapi.registration

import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

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
        @RequestBody(required = true) playerName: PlayerNameJson,
        builder: UriComponentsBuilder
    ): ResponseEntity<PlayerNameJson> {
        // TODO extract sub + provider from authenticated JWT token (because I'm not sure if providers guarantee universally unique identifiers)
        // otherwise if someone logs in with Facebook, and gets sub 1234
        // and somebody completely different logs in with Google, and also gets sub 1234
        // both of these people (who are physically different people, aka different players) will be able to log in to each others account

        val registerPlayer = RegisterPlayer(nickname = playerName.username)
        val registeredPlayer = commandExecutor.execute(registerPlayer)

        val locationUri = builder.path("/api/player/{id}").buildAndExpand(registeredPlayer.id).toUri()

        return ResponseEntity.created(locationUri).body(playerName)
    }
}

@RestController
@RequestMapping(
    "/api/player",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class PlayerController(
    private val queryExecutor: QueryExecutor,
) {

    @GetMapping(path = ["/{id}"])
    fun getPlayerById(
        @PathVariable id: PlayerId
    ): ResponseEntity<RegisteredPlayerJson> {
        val player = queryExecutor.execute(PlayerById(id), RegisteredPlayerRepresentation::toJson)

        return ResponseEntity.ok(player)
    }
}


@RestController
@RequestMapping(
    "/api/challenge",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ChallengeController(
    private val commandExecutor: CommandExecutor,
    private val queryExecutor: QueryExecutor,
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun challengePlayer(@RequestBody(required = true) challengeRequest: ChallengePlayerJson): ResponseEntity<String> {

        commandExecutor.execute(ChallengePlayer(challengeRequest.initiator, challengeRequest.opponent))

        val opponent: RegisteredPlayerJson = queryExecutor
            .execute(PlayerById(challengeRequest.opponent), RegisteredPlayerRepresentation::toJson)


        return ResponseEntity.ok("Player $opponent was successfully challenged")
    }
}

private fun RegisteredPlayerRepresentation.toJson(): RegisteredPlayerJson = RegisteredPlayerJson(this.id, this.nickname)


data class PlayerNameJson(val username: String)
data class RegisteredPlayerJson(val playerId: UUID, val nickname: String)
data class ChallengePlayerJson(val initiator: UUID, val opponent: UUID)
