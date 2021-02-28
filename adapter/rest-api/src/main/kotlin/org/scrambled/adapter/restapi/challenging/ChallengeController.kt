package org.scrambled.adapter.restapi.challenging

import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.adapter.restapi.players.toJson
import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
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

data class ChallengePlayerJson(val initiator: UUID, val opponent: UUID)
