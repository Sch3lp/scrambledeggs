package org.scrambled.scenariotests.steps.core

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.scrambled.adapter.restapi.challenging.ChallengeRequestJson
import org.scrambled.domain.core.api.challenging.ChallengeId
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl

suspend fun HttpClient.challengePlayerStep(challenger: PlayerId, opponent: PlayerId): ApiResult<ChallengeId> {
    val response = this.post<HttpResponse> {
        url("$baseUrl/challenge")
        contentType(ContentType.Application.Json)
        body = ChallengeRequestJson(challenger, opponent)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toChallengeId().asApiResult(response)
}
