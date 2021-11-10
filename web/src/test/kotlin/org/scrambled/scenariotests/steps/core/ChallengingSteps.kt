package org.scrambled.scenariotests.steps.core

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.scrambled.matches.adapters.`in`.rest.challenging.ChallengeRequestJson
import org.scrambled.matches.adapters.`in`.rest.challenging.PendingChallengeJson
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.scrambled.matches.domain.api.challenges.GameMode
import org.scrambled.matches.domain.api.challenges.PlayerId
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl

suspend fun HttpClient.challengePlayerStep(
    challenger: PlayerId,
    opponent: PlayerId,
    comment: String,
    appointmentSuggestion: String,
    gameMode: GameMode,
): ApiResult<ChallengeId> {
    val response = this.post<HttpResponse> {
        url("$baseUrl/challenge")
        contentType(ContentType.Application.Json)
        body = ChallengeRequestJson(challenger, opponent, comment, appointmentSuggestion, gameMode)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toChallengeId().asApiResult(response)
}

suspend fun HttpClient.fetchPendingChallengesStep(): List<PendingChallengeJson> {
    return this.get {
        url("$baseUrl/challenge/pending")
        contentType(ContentType.Application.Json)
    }
}

suspend fun HttpClient.acceptChallengeStep(challengeId: String) : Unit {
    val response = this.put<HttpResponse> {
        url("$baseUrl/challenge/${challengeId}/accept") // Thanks Socrates OpenSpace for collabing on a good endpoint
        contentType(ContentType.Application.Json)
        expectSuccess = true
    }
}

