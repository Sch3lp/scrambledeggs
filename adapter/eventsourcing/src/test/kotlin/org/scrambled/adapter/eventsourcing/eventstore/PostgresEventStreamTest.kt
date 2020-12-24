package org.scrambled.adapter.eventsourcing.eventstore

import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider.OPTIONS
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.scrambled.adapter.eventsourcing.PostgresEventStoreTestConfig
import org.scrambled.adapter.eventsourcing.api.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
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

    @Autowired
    lateinit var eventStream: PostgresEventStream

    @BeforeAll
    fun beforeAll() {
        eventStream = PostgresEventStream(postgresClient())
    }

    @Test
    fun `Pushing an event on the eventstream is persisted in Postgres`() = runBlocking {
        eventStream.push(Event.PlayerRegistered("Mumra"))
    }
}