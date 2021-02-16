package org.scrambled.adapter.eventsourcing.leaderboards

import kotlinx.coroutines.runBlocking
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.domain.leaderboards.api.BroadcastEvent
import org.scrambled.domain.leaderboards.api.BroadcastEvents
import org.springframework.stereotype.Component

@Component
class BroadcastEventsStore(
    private val eventStore: EventStore
): BroadcastEvents {
    override fun keep(event: BroadcastEvent) = when(event) {
        is BroadcastEvent.PlayerRegisteredForLeaderboard -> Event.PlayerRegistered(event.nickname)
    }.store()

    fun Event.store() = runBlocking { eventStore.push(this@store) }

}