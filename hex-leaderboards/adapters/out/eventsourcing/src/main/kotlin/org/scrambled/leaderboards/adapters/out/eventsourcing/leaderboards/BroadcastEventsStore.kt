package org.scrambled.leaderboards.adapters.out.eventsourcing.leaderboards

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvent
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvents
import org.springframework.stereotype.Component

@Component
class BroadcastEventsStore(
    private val eventStore: EventStore
): BroadcastEvents {
    override fun keep(event: BroadcastEvent) = when(event) {
        is BroadcastEvent.PlayerRegisteredForLeaderboard -> Event.PlayerRegistered(event.playerId, event.nickname)
        is BroadcastEvent.PlayerChallengedForLeaderboard -> Event.PlayerChallenged(event.challenger, event.opponent)
    }.store()

    override fun findAll(): List<BroadcastEvent> = runBlocking {
        val all = mutableListOf<BroadcastEvent>()
        eventStore.collect { event ->
            when(event) {
                is Event.PlayerRegistered -> BroadcastEvent.PlayerRegisteredForLeaderboard(event.playerId, event.nickname)
                is Event.PlayerRenamed -> null
                is Event.PlayerChallenged -> BroadcastEvent.PlayerChallengedForLeaderboard(event.challenger, event.opponent)
            }?.let { broadcastEvent -> all += broadcastEvent }
        }
        return@runBlocking all
    }

    private fun Event.store() = runBlocking { eventStore.push(this@store) }

}