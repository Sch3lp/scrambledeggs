package org.scrambled.scenariotests.steps.core

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.scrambled.adapter.restapi.players.RegisterPlayerJson
import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.scenariotests.steps.client.baseUrl
import org.scrambled.scenariotests.steps.client.client

suspend fun registerPlayerStep(playerNickname: String): PlayerId {
    val response = client.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        body = RegisterPlayerJson(playerNickname)
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId()
        ?: throw RuntimeException("Registering $playerNickname failed with status ${response.status}")
}

suspend fun fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    return client.get {
        url("$baseUrl/player/$playerId")
        contentType(ContentType.Application.Json)
    }
}