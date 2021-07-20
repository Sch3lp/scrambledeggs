package org.scrambled.scenariotests.steps.core

import io.ktor.client.features.*
import io.ktor.client.features.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.scrambled.adapter.restapi.JwtInfo
import org.scrambled.adapter.restapi.players.RegisterPlayerJson
import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.adapter.restapi.security.SecurityConfig
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl
import org.scrambled.scenariotests.steps.client.client
import java.util.*

suspend fun exchangeCookie(jwtInfo: JwtInfo) {
    client.get<HttpResponse> {
        url("$baseUrl/session")
        header(HttpHeaders.Authorization, "Bearer ${jwtInfo.asDummyEncodedJwt()}")
        expectSuccess = false
    }
}


suspend fun registerPlayerStep(playerNickname: String): ApiResult<PlayerId> {
    val response = client.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        body = RegisterPlayerJson(playerNickname)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId().asApiResult(response)
}

suspend fun fetchPlayerByJwtInfoStep(): RegisteredPlayerJson? {
    println("Fetching playerin🍪🍪🍪")
    client.cookies("localhost").forEach { println(it) }
    val players: List<RegisteredPlayerJson> = client.get {
        url("$baseUrl/player/info")
        contentType(ContentType.Application.Json)
    }
    return players.firstOrNull()
}

suspend fun fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    println("🍪🍪🍪")
    client.cookies("localhost").forEach { println(it) }
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
