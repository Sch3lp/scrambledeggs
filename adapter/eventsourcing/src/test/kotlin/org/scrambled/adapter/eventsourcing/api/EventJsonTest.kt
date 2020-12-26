package org.scrambled.adapter.eventsourcing.api

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EventJsonTest {
    @Test
    fun `PlayerRegistered can be serialized`() {
        val json = Event.PlayerRegistered("Snarf").asJson()

        assertThat(json).contains("\"nickname\":\"Snarf\"").contains("\"type\":\"PlayerRegistered\"")
    }

    @Test
    fun `PlayerRegistered can be serialized in a coroutine`() {
        val json =
            suspend {
                Event.PlayerRegistered("Snarf").asJsonBlocking()
            }

        runBlocking { assertThat(json.invoke()).contains("\"nickname\":\"Snarf\"").contains("\"type\":\"PlayerRegistered\"") }
    }

    @Test
    fun `PlayerRegistered can be deserialized`() {
        val playerRegistered = Event.PlayerRegistered("Snarf")
        val json = playerRegistered.asJson()

        val deserialized = json.fromJson<Event.PlayerRegistered>()
        assertThat(deserialized.also { println(it) }).isEqualTo(playerRegistered)
        assertThat(deserialized.nickname).isEqualTo("Snarf")
    }

    @Test
    fun `PlayerRegistered can be deserialized in a coroutine`() {
        val playerRegistered = Event.PlayerRegistered("Snarf")
        val json = playerRegistered.asJson()

        val deserialized = suspend { json.fromJson<Event.PlayerRegistered>() }
        runBlocking { assertThat(deserialized.invoke()).isEqualTo(playerRegistered) }
    }
}