package org.scrambled.matches.adapters.`in`.rest.challenging

import org.scrambled.common.adapter.restapi.extensions.toExternalAccountRef
import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.*
import org.scrambled.matches.domain.api.players.PlayerByExternalAccountRef
import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

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
    fun challengePlayer(
        @RequestBody(required = true) challengeRequest: ChallengeRequestJson,
        builder: UriComponentsBuilder
    ): ResponseEntity<String> {
        val createdChallengeId = commandExecutor.execute(challengeRequest.toCommand())

        val locationUri = builder.path("/api/challenge/{id}").buildAndExpand(createdChallengeId).toUri()

        return ResponseEntity.created(locationUri).build()
    }

    @GetMapping("/pending/{challengeId}")
    fun pendingChallenges(@PathVariable challengeId: ChallengeId): ResponseEntity<PendingChallengeDetailJson> {
        val externalAccountRef = SecurityContextHolder.getContext().toExternalAccountRef()
        val player = queryExecutor.executeOrNull(PlayerByExternalAccountRef(externalAccountRef)) { this }
        return player?.let {
            val pendingChallenge =
                queryExecutor.execute(PendingChallengeById(challengeId)) { toDetailJson(this, player.id) }
            ResponseEntity.ok(pendingChallenge)
        }
            ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/pending")
    fun pendingChallenges(): List<PendingChallengeJson> {
        val externalAccountRef = SecurityContextHolder.getContext().toExternalAccountRef()
        val player = queryExecutor.executeOrNull(PlayerByExternalAccountRef(externalAccountRef)) { this }
        return player?.let { p ->
            //TODO pass externalAccountRef instead of playerId (and move player does not exist handling to the handler)
            queryExecutor.execute(PendingChallengesFor(p.id)) { this.map(::toJson) }
        } ?: emptyList()
    }

    @PutMapping("{challengeId}/accept")
    fun acceptChallenge(@PathVariable challengeId: ChallengeId): ResponseEntity<Any> {
        commandExecutor.execute(AcceptChallenge(challengeId))
        return ResponseEntity.ok().build()
    }

    fun toJson(rep: QueryablePendingChallenge) = PendingChallengeJson(
        rep.challengeId,
        rep.gameMode,
        rep.opponentName,
        rep.appointment,
        rep.comment,
    )

    fun toDetailJson(challenge: QueryablePendingChallenge, id: PlayerId): PendingChallengeDetailJson {
        val challengeText = if (challenge.opponentId == id) {
            "${challenge.challengerName} challenged you"
        } else {
            "You challenged ${challenge.opponentName}"
        }
        return PendingChallengeDetailJson(
            challenge.challengeId,
            challenge.gameMode,
            challengeText,
            challenge.appointment,
            challenge.comment,
        )
    }

    private fun ChallengeRequestJson.toCommand() =
        ChallengePlayer(
            this.challenger,
            this.opponent,
            UsefulString(this.comment),
            UsefulString(this.appointmentSuggestion),
            this.gameMode
        )
}

data class ChallengeRequestJson(
    val challenger: PlayerId,
    val opponent: PlayerId,
    val comment: String,
    val appointmentSuggestion: String,
    val gameMode: GameMode,
)

data class PendingChallengeJson(
    val challengeId: String,
    val gameMode: GameMode,
    val opponentName: String,
    val appointment: String,
    val comment: String,
)

data class PendingChallengeDetailJson(
    val challengeId: String,
    val gameMode: GameMode,
    val challengeText: String,
    val appointment: String,
    val comment: String,
)