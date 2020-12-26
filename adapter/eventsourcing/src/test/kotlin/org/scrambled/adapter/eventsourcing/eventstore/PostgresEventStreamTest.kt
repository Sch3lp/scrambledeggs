package org.scrambled.adapter.eventsourcing.eventstore

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.fromJson
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.RowsFetchSpec
import org.testcontainers.junit.jupiter.Testcontainers


const val DB_PORT = 6667
const val DB_NAME = "postgreseventsdb"
const val DB_USERNAME = "snarf"
const val DB_PASSWORD = "Lion-0!"

@Testcontainers
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
//@SpringJUnitConfig(classes = [PostgresEventStoreTestConfig::class])
class PostgresEventStreamTest {
//
//    @Container
//    var postgres = PostgreSQLContainer<PostgreSQLContainer<*>>("postgres:13.1-alpine")
//        .apply {
//            withExposedPorts(DB_PORT)
//            withDatabaseName(DB_NAME)
//            withUsername(DB_USERNAME)
//            withPassword(DB_PASSWORD)
//            withInitScript("db/migrations/V1__CreateEventStoreTable.sql")
//        }

//    @Autowired
//    lateinit var client: DatabaseClient

//    @Bean
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

    @Test
    fun `Pushing an event on the eventstream is persisted in Postgres`() = runBlocking {
        eventStream.push(Event.PlayerRegistered("Mumra"))

        val map: RowsFetchSpec<String> = postgresClient.sql { "select payload from eventstore" }
            .map { row -> row.get("payload", String::class.java) }
        val map1 = map.all().map { it.fromJson<Event.PlayerRegistered>() }
        val lastEvent = map1.blockLast()
        assertThat(lastEvent?.nickname).isEqualTo("Mumra")

        return@runBlocking
    }
}