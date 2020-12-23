package org.scrambled.adapter.eventsourcing.eventstore

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.spi.Row
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.reactive.asFlow
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class PostgresEventStream(private val client: DatabaseClient) : EventStore {

    override suspend fun push(event: Event) =
        client.sql { "INSERT INTO eventstore values($1, $2, $3)" }
            .bind("$1", event.id)
            .bind("$2", event.at)
            .bind("$3", event.asJson())
            .await()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<Event>) {
        return client.sql { "SELECT payload FROM eventstore" }
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