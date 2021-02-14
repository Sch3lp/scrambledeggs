package org.scrambled.adapter.eventsourcing.leaderboards

import kotlinx.coroutines.runBlocking
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.domain.leaderboards.LeaderboardEvent
import org.scrambled.domain.leaderboards.LeaderboardEvents
import org.springframework.stereotype.Component

@Component
class MostChallengesLeaderboardStore(
    private val eventStore: EventStore
): LeaderboardEvents {
    override fun keep(event: LeaderboardEvent) = when(event) {
        is LeaderboardEvent.PlayerRegisteredForLeaderboard -> Event.PlayerRegistered(event.nickname)
    }.store()

    fun Event.store() = runBlocking { eventStore.push(this@store) }

}