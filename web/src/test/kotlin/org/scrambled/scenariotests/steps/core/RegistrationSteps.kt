package org.scrambled.scenariotests.steps.core

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.scrambled.adapter.restapi.players.RegisterPlayerJson
import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenges.ChallengeId
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl
import java.util.*

suspend fun HttpClient.registerPlayerStep(playerNickname: String): ApiResult<PlayerId> {
    val response = this.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        body = RegisterPlayerJson(playerNickname)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId().asApiResult(response)
}

suspend fun HttpClient.fetchPlayerByJwtInfoStep(): RegisteredPlayerJson? {
    val players: List<RegisteredPlayerJson> = this.get {
        url("$baseUrl/player/info")
        contentType(ContentType.Application.Json)
    }
    return players.firstOrNull()
}

suspend fun HttpClient.fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    return this.get {
        url("$baseUrl/player/$playerId")
        contentType(ContentType.Application.Json)
    }
}

suspend fun HttpClient.fetchAllPlayersStep(): List<RegisteredPlayerJson> {
    return this.get {
        url("$baseUrl/player")
        contentType(ContentType.Application.Json)
    }
}

internal fun String.toPlayerId() : PlayerId = UUID.fromString(this)
internal fun String.toChallengeId() : ChallengeId = ChallengeId.challengeId(this)