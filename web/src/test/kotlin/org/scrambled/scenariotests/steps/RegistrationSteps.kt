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
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.error.AssertionErrorCreator
import org.scrambled.adapter.restapi.registration.PlayerNameJson
import org.scrambled.adapter.restapi.registration.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.infra.cqrs.DomainEventBroadcaster
import org.springframework.http.MediaType
import org.springframework.web.servlet.function.RequestPredicates.contentType

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            registerModules(KotlinModule(), JavaTimeModule())
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}

suspend fun registerPlayerStep(playerNickname: String): PlayerId {
    val response = client.post<HttpResponse> {
        url("http://localhost:9999/api/register")
        contentType(ContentType.Application.Json)
        body = PlayerNameJson(playerNickname)
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId()
        ?: throw RuntimeException("Registering $playerNickname failed with status ${response.status}")
}

suspend fun fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    return client.get {
        url("http://localhost:9999/api/player/$playerId")
        contentType(ContentType.Application.Json)
    }
}

fun DomainEventBroadcaster.verifyRegisteredPlayer(playerNickname: String) {
    val playerRegisteredEvent = this.findEvent(PlayerRegistered::class.java)
    assertThat(playerRegisteredEvent?.nickName).isEqualTo(playerNickname)
}