package org.scrambled.adapter.eventsourcing.eventstore

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.Row
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.reactive.asFlow
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import reactor.core.publisher.Flux


class PostgresEventStream(r2dbcUrl: String = "r2dbc:h2:mem:///testdb") : EventStore {

    private val client by lazy { DatabaseClient.create(ConnectionFactories.get(r2dbcUrl)) }

    override suspend fun push(event: Event) =
        client.sql { "INSERT INTO EVENTSTORE values($1)" }
            .bind("$1", event.asJson())
            .await()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<Event>) {
        return client.sql { "SELECT payload FROM EVENTSTORE" }
            .map { row -> row.getString(0)?.let { it -> jacksonObjectMapper().readValue<Event>(it) } }
            .all()
            .filterNonNull()
            .asFlow()
            .collect(collector)
    }
}

fun Row.getString(index: Int) = get(index, String::class.java)
fun <T> Flux<T?>.filterNonNull(): Flux<T> = filter { it != null }.map { it!! }

fun Event.asJson() = lazy { jacksonObjectMapper().writeValueAsString(this) }