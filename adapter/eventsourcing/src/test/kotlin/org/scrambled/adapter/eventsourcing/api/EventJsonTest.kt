package org.scrambled.adapter.eventsourcing.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EventJsonTest {
    @Test
    internal fun `PlayerRegistered can be serialized`() {
        val json = Event.PlayerRegistered("Snarf").asJson()

        assertThat(json).isEqualTo("{\"nickname\":\"Snarf\",\"id\":\"859a77ab-96aa-4e38-8571-545a2b72fa9c\",\"at\":[2020,12,24,17,7,55,849986000],\"type\":\"PlayerRegistered\"}")
    }

    @Test
    internal fun `PlayerRegistered can be serialized in a coroutine`() {
        val json = suspend {
            Event.PlayerRegistered("Snarf").asBlocking()
        }

        assertThat(json).isEqualTo("{\"nickname\":\"Snarf\",\"id\":\"859a77ab-96aa-4e38-8571-545a2b72fa9c\",\"at\":[2020,12,24,17,7,55,849986000],\"type\":\"PlayerRegistered\"}")
    }
}