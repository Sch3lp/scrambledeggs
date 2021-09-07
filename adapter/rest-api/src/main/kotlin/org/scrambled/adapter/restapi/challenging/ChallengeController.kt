package org.scrambled.adapter.restapi.challenging

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.infra.cqrs.CommandExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping(
    "/api/challenge",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ChallengeController(
    private val commandExecutor: CommandExecutor,
) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun challengePlayer(@RequestBody(required = true) challengeRequest: ChallengeRequestJson,
                        builder: UriComponentsBuilder
    ): ResponseEntity<String> {
        val createdChallengeId = commandExecutor.execute(challengeRequest.toCommand())

        val locationUri = builder.path("/api/challenge/{id}").buildAndExpand(createdChallengeId).toUri()

        return ResponseEntity.created(locationUri).build()
    }

    private fun ChallengeRequestJson.toCommand() =
        ChallengePlayer(this.challenger, this.opponent, UsefulString(this.comment), UsefulString(this.appointmentSuggestion))
}

data class ChallengeRequestJson(
    val challenger: PlayerId,
    val opponent: PlayerId,
    val comment: String,
    val appointmentSuggestion: String,
)
