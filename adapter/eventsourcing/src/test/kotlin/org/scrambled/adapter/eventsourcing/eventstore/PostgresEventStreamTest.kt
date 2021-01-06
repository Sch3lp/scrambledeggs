package org.scrambled.adapter.eventsourcing.eventstore

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.scrambled.adapter.eventsourcing.api.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.testcontainers.junit.jupiter.Testcontainers

@DataR2dbcTest
@Testcontainers
class PostgresEventStoreSpringWiringTest {

    @Autowired
    private lateinit var eventStream: PostgresEventStore
    @Autowired
    private lateinit var postgresClient: DatabaseClient


    @BeforeEach
    fun setUp() {
        runBlocking { postgresClient.sql { "delete from eventstore where 1=1" }.await() }
    }

    @Test
    fun `Pushing an event on the eventstream is persisted in Postgres`() {
        val newPlayerRegisteredEvent = Event.PlayerRegistered(randomString(10))
        runBlocking { eventStream.push(newPlayerRegisteredEvent) }
        runBlocking {
            val mostRecentEvent = eventStream.mostRecent<Event.PlayerRegistered>()
            assertThat(mostRecentEvent).isEqualTo(newPlayerRegisteredEvent)
        }
    }

    @Test
    fun `Getting the mostRecent event of a specific type ignores other events`() {
        val nickname = randomString(10)
        val newPlayerRegisteredEvent = Event.PlayerRegistered(nickname)
        runBlocking { eventStream.push(newPlayerRegisteredEvent) }
        runBlocking { eventStream.push(Event.PlayerRenamed(nickname, randomString(10))) }
        runBlocking {
            val mostRecentEvent = eventStream.mostRecent<Event.PlayerRegistered>()
            assertThat(mostRecentEvent).isEqualTo(newPlayerRegisteredEvent)
        }
    }

    @Test
    fun `Getting the mostRecent event of a specific type when there was never such an event returns nothing`() {
        runBlocking { eventStream.push(Event.PlayerRenamed(randomString(10), randomString(10))) }
        runBlocking {
            val mostRecentEvent = eventStream.mostRecent<Event.PlayerRegistered>()
            assertThat(mostRecentEvent).isNull()
        }
    }
}

fun randomString(randomStringLength: Int = 1): String {
    val chars = 'A'..'z'
    return (0..randomStringLength).fold("${chars.random()}") { acc, _ -> acc + chars.random() }
}