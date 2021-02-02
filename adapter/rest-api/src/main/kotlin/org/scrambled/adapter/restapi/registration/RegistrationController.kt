package org.scrambled.adapter.restapi.registration

import org.scrambled.adapter.restapi.exceptionhandling.CustomException
import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(
    "/api/register",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class RegistrationController(
    private val commandExecutor: CommandExecutor,
    private val queryExecutor: QueryExecutor,
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun registerPlayer(@RequestBody(required = true) playerName: PlayerNameJson): ResponseEntity<PlayerNameJson> {
        // TODO extract sub + provider from authenticated JWT token (because I'm not sure if providers guarantee universally unique identifiers)
        // otherwise if someone logs in with Facebook, and gets sub 1234
        // and somebody completely different logs in with Google, and also gets sub 1234
        // both of these people (who are physically different people, aka different players) will be able to log in to each others account
        val sub = "1234"

//        throw CustomException("This is a message from the backend that will be shown in the frontend")

        commandExecutor.execute(RegisterPlayer(playerName.username))

        return ResponseEntity.ok(playerName)
    }
}

data class PlayerNameJson(val username: String)

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
            .execute(PlayerById(challengeRequest.opponent)) {
                RegisteredPlayerJson(
                    it.id,
                    it.nickname
                )
            } ?: throw CustomException("Couldn't find stuff")

        return ResponseEntity.ok("Player $opponent was successfully challenged")
    }
}

data class ChallengePlayerJson(val initiator: UUID, val opponent: UUID)
data class RegisteredPlayerJson(val playerId: UUID, val nickName: String)
