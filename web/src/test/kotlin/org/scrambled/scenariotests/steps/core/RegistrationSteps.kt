package org.scrambled.scenariotests.steps.core

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.junit.jupiter.api.fail
import org.scrambled.adapter.restapi.players.RegisterPlayerJson
import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl
import org.scrambled.scenariotests.steps.client.client

data class JwtInfo(val jwtIss: JwtIss, val jwtSub: JwtSub)

suspend fun registerPlayerStep(playerNickname: String, jwtInfo: JwtInfo): ApiResult<PlayerId> {
    if (fetchPlayerByJwtInfoStep(jwtInfo) != null) {
        fail("You're trying to register a player, but there's already a player registered with the same ExternalAccountRef: $jwtInfo")
    }
    val response = client.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        body = RegisterPlayerJson(playerNickname, jwtInfo.jwtIss, jwtInfo.jwtSub)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId().asApiResult(response)
}

suspend fun fetchPlayerByJwtInfoStep(jwtInfo: JwtInfo): RegisteredPlayerJson? {
    fun JwtInfo.asQueryParams(): String = "jwtIss=${jwtInfo.jwtIss}&jwtSub=${jwtInfo.jwtSub}"
    val players: List<RegisteredPlayerJson> = client.get {
        url("$baseUrl/player?${jwtInfo.asQueryParams()}")
        contentType(ContentType.Application.Json)
    }
    return players.firstOrNull()
}

suspend fun fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    return client.get {
        url("$baseUrl/player/$playerId")
        contentType(ContentType.Application.Json)
    }
}

suspend fun fetchAllPlayersStep(): List<RegisteredPlayerJson> {
    return client.get {
        url("$baseUrl/player")
        contentType(ContentType.Application.Json)
    }
}