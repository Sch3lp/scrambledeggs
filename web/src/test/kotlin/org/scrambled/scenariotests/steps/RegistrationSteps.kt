package org.scrambled.scenariotests.steps

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.scrambled.adapter.restapi.registration.PlayerNameJson
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

suspend fun registerPlayerStep(playerNickname: String) {
    client.post<Unit> {
        url("http://localhost:9999/api/register")
        contentType(ContentType.Application.Json)
        body = PlayerNameJson(playerNickname)
    }
}

fun DomainEventBroadcaster.verifyRegisteredPlayer(playerNickname: String) {
    val playerRegisteredEvent = this.findEvent(PlayerRegistered::class.java)
    assertThat(playerRegisteredEvent?.nickName).isEqualTo(playerNickname)
}