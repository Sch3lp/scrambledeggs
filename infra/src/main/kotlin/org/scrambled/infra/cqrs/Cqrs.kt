package org.scrambled.infra.cqrs

import org.scrambled.infra.domainevents.DomainEvent
import org.scrambled.infra.domainevents.IDomainEventBroadcaster
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.KClass

typealias AggregateId = UUID

@Component
class CommandExecutor(
    private val commandHandlers: List<CommandHandler<*, *>>,
    private val domainEventBroadcaster: IDomainEventBroadcaster
) {
    fun <R> execute(command: Command<R>): R {
        val handler = handlerForCommand(command)
        val (result, domainEvent) = handler.handle(command)
        domainEventBroadcaster.publish(domainEvent)
        return result
    }

    private inline fun <R, reified Cmd : Command<R>> handlerForCommand(command: Cmd) =
        (commandHandlers as List<CommandHandler<R, Cmd>>)
            .first { handler -> command::class == handler.commandType }
}

interface Command<R>

@Transactional(transactionManager = "rdbms-tx-mgr")
interface CommandHandler<R, Cmd : Command<R>> {
    val commandType: KClass<Cmd>
    fun handle(cmd: Cmd): Pair<R, DomainEvent>
}


@Component
class QueryExecutor(private val queryHandlers: List<QueryHandler<*, *>>) {
    //TODO Refactor to not return R, but rather a Maybe<R>, because we cannot express nullable type information in generics
    //We can't go like "hey, give me an R? and both an R with the same function name" (function overloading doesn't work with nullable types)
    fun <Agg, R> execute(query: Query<Agg>, transform: Agg.() -> R): R {
        return handlerForQuery(query)
            .handle(query)
            ?.transform()
            ?: throw GenericNotFoundException("The query's implementation was supposed to throw the not found exception")
    }

    fun <Agg, R> executeOrNull(query: Query<Agg>, transform: Agg.() -> R): R? {
        return handlerForQuery(query)
            .handle(query)
            ?.transform()
    }

    private inline fun <reified Q : Query<R>, R> handlerForQuery(query: Q) =
        (queryHandlers as List<QueryHandler<Q, R>>)
            .first { handler -> query::class == handler.queryType }
}

interface Query<Aggregate> {
    val id: AggregateId
}

@Transactional(transactionManager = "rdbms-tx-mgr")
interface QueryHandler<Q : Query<Representation>, Representation> {
    val queryType: KClass<Q>
    fun handle(query: Q): Representation?
}

class GenericNotFoundException(message: String, cause: Error? = null) : RuntimeException(message, cause)