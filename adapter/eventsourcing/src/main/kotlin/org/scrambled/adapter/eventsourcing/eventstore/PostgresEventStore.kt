package org.scrambled.adapter.eventsourcing.eventstore

import io.r2dbc.spi.Row
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore
import org.scrambled.adapter.eventsourcing.api.asJson
import org.scrambled.adapter.eventsourcing.api.fromJson
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class PostgresEventStore(val client: DatabaseClient) : EventStore {

    override suspend fun push(event: Event) =
        client.sql { "INSERT INTO eventstore values($1, $2, $3::JSON)" }
            .bind("$1", event.id)
            .bind("$2", event.at)
            .bind("$3", event.asJson())
            .await()

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<Event>) {
        return client.sql { "SELECT payload FROM eventstore" }
            .map { row -> row.getString(0)!!.fromJson<Event>() }
            .all()
            .asFlow()
            .collect(collector)
    }

    final suspend inline fun <reified T> mostRecent(): T? = client
        .sql { "select payload from eventstore order by at desc" }
        .map { row -> row.get("payload", String::class.java)!!.fromJson<Event>() }
        .all()
        .asFlow()
        .filterIsInstance<T>()
        .firstOrNull()
}

fun Row.getString(index: Int) = get(index, String::class.java)
fun <T> Flux<T?>.filterNonNull(): Flux<T> = filter { it != null }.map { it!! }

