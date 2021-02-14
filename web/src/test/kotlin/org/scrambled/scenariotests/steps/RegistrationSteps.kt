package org.scrambled.scenariotests.steps

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.assertj.core.api.Assertions.assertThat
import org.scrambled.adapter.restapi.registration.PlayerNameJson
import org.scrambled.adapter.restapi.registration.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.infra.cqrs.InMemoryDomainEventBroadcaster

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            registerModules(KotlinModule(), JavaTimeModule())
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

val baseUrl = "http://localhost:9999/api"

suspend fun registerPlayerStep(playerNickname: String): PlayerId {
    val response = client.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        body = PlayerNameJson(playerNickname)
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

fun InMemoryDomainEventBroadcaster.verifyRegisteredPlayer(playerNickname: String) {
    val playerRegisteredEvent = this.findEvent(PlayerRegistered::class.java)
    assertThat(playerRegisteredEvent?.nickName).isEqualTo(playerNickname)
}