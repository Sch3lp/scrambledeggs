package org.scrambled.adapter.rdbms.projection.leaderboard

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.core.kotlin.mapTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.adapter.eventsourcing.eventstore.InMemoryEventStore

internal class LeaderboardProjectionsTest {

    lateinit var projections: LeaderboardProjections
    lateinit var eventStore: EventStore

    @BeforeEach
    internal fun setUp() {
        eventStore = InMemoryEventStore()
        projections = LeaderboardProjections(eventStore)
    }

    @Test
    internal fun `when a single player registered, we want to create a leaderboard with that player in it`() {
        runBlocking { eventStore.push(Event.PlayerRegistered("CoreDusk")) }
        runBlocking { projections.subscribePlayerRegisteredSystemEvent() }

        val result = "select * from leaderboard".execute<LeaderboardRow>()

        assertThat(result).containsExactly(LeaderboardRow(1,"CoreDusk",0))
    }

    @Test
    internal fun `when a second player registered, the leaderboard should have all registered players in it, with the second player at the bottom`() {

    }
}

data class LeaderboardRow(val rank: Int, val playerName: String, val score: Int)

inline fun <reified T : Any> String.execute(): List<T> {
    val jdbi = Jdbi.create("jdbc:postgresql://localhost:6669/postgresprojectionsdb", "mumra", "SnarfSnarf!")
    val handle = jdbi.installPlugin(KotlinPlugin()).open()

    return handle.createQuery(this).mapTo<T>().list()
}