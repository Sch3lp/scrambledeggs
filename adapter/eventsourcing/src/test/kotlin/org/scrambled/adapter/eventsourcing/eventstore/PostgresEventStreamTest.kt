package org.scrambled.adapter.eventsourcing.eventstore

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.scrambled.adapter.eventsourcing.api.Event
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await


const val DB_PORT = 6667
const val DB_NAME = "postgreseventsdb"
const val DB_USERNAME = "snarf"
const val DB_PASSWORD = "Lion-0!"

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class PostgresEventStreamTest {
    fun postgresClient(): DatabaseClient {
        val options: MutableMap<String, String> = HashMap()
        options["lock_timeout"] = "10s"

        val connectionFactory: ConnectionFactory = ConnectionFactories.get(
            builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, DB_PORT) // optional, defaults to 5432
                .option(USER, DB_USERNAME)
                .option(PASSWORD, DB_PASSWORD)
                .option(DATABASE, DB_NAME) // optional
                .option<Map<String, String>>(OPTIONS, options) // optional
                .build()
        )
        return DatabaseClient.create(connectionFactory)
    }

    lateinit var postgresClient: DatabaseClient

    lateinit var eventStream: PostgresEventStream

    @BeforeAll
    fun beforeAll() {
        postgresClient = postgresClient()
        eventStream = PostgresEventStream(postgresClient)
    }

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
        val newPlayerRegisteredEvent = Event.PlayerRegistered(randomString(10))
        runBlocking { eventStream.push(newPlayerRegisteredEvent) }
        runBlocking { eventStream.push(Event.PlayerRenamed(randomString(10))) }
        runBlocking {
            val mostRecentEvent = eventStream.mostRecent<Event.PlayerRegistered>()
            assertThat(mostRecentEvent).isEqualTo(newPlayerRegisteredEvent)
        }
    }

    @Test
    fun `Getting the mostRecent event of a specific type when there was never such an event returns nothing`() {
        runBlocking { eventStream.push(Event.PlayerRenamed(randomString(10))) }
        runBlocking {
            val mostRecentEvent = eventStream.mostRecent<Event.PlayerRegistered>()
            assertThat(mostRecentEvent).isNull()
        }
    }
}

fun randomString(i: Int = 1): String {
    val chars = 'A'..'z'
    return (0..i).fold("${chars.random()}") { acc, _ -> acc + chars.random() }
}