package org.scrambled.adapter.rdbms.projection.leaderboard

import kotlinx.coroutines.flow.collect
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.adapter.eventsourcing.api.filterEvents

class LeaderboardProjections(private val eventStore: EventStore) {

    val handle: Handle

    init {
        val jdbi = Jdbi.create("jdbc:postgresql://localhost:6669/postgresprojectionsdb", "mumra", "SnarfSnarf!")
        handle = jdbi.installPlugin(KotlinPlugin()).open()
    }

    //subscribes to system-events (not events that are going to be stored in the eventStore!!!!)
    suspend fun subscribePlayerRegisteredSystemEvent() {
        eventStore.filterEvents<Event.PlayerRegistered>().collect {
            addPlayerToLeaderboard(it.nickname)
        }
    }

    private fun addPlayerToLeaderboard(nickname: String) {
        handle.execute("insert into leaderboard(rank, playername, score) values (?,?,?)", 1, nickname, 0)
    }
}