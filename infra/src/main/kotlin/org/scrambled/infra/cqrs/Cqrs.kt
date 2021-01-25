package org.scrambled.infra.cqrs

import java.util.*
import kotlin.reflect.KClass

typealias AggregateId = UUID

interface Command<Aggregate : Any> {
    val id: AggregateId
    val aggregate: KClass<Aggregate>

    fun Aggregate.execute(): DomainEvent
}

class CommandExecutor {
    fun <Agg : Any> execute(command: Command<Agg>) {
        repositoryForAggregate(command.aggregate).getById(command.id)
    }
}

typealias DomainEventId = UUID

abstract class DomainEvent(private val id: DomainEventId = UUID.randomUUID())

fun <Agg : Any> repositoryForAggregate(aggregate: KClass<Agg>): Repository<Agg> {
    val repositories: List<Repository<Agg>> = listOf()
    return repositories.filterIsInstance(Repository<aggregate.java>).first()
}

interface Repository<Aggregate> {
    fun getById(id: AggregateId): Aggregate?
}


interface Query<Aggregate : Any> {
    val id: AggregateId
    val aggregate: KClass<Aggregate>
}

class QueryExecutor {
    fun <Agg : Any, R> execute(query: Query<Agg>, transformer: (Agg) -> R): R? {
        return repositoryForAggregate(query.aggregate).getById(query.id)?.let { transformer(it) }
    }
}