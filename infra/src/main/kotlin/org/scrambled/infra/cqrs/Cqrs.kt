package org.scrambled.infra.cqrs

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.reflect.KClass

typealias AggregateId = UUID

@Component
class CommandExecutor(
    private val commandHandlers: List<CommandHandler<*,*>>,
    private val domainEventBroadcaster: DomainEventBroadcaster
) {
    fun <R> execute(command: Command<R>): R {
        val handler = handlerForCommand(command)
        val (result, domainEvent) = handler.handle(command)
        domainEventBroadcaster.publish(domainEvent)
        return result
    }
    private inline fun <R, reified Cmd: Command<R>> handlerForCommand(command: Cmd) =
        (commandHandlers as List<CommandHandler<R, Cmd>>)
            .first { handler -> command::class == handler.commandType }
}
interface Command<R>

@Transactional(transactionManager = "rdbms-tx-mgr")
interface CommandHandler<R, Cmd: Command<R>> {
    val commandType: KClass<Cmd>
    fun handle(cmd: Cmd): Pair<R, DomainEvent>
}




@Component
class QueryExecutor(private val queryHandlers: List<QueryHandler<*, *>>) {
    fun <Agg : Any, R> execute(query: Query<Agg>, transform: Agg.() -> R): R {
        return handlerForQuery(query)
            .handle(query)
            .transform()
    }
    private inline fun <reified Q: Query<R>, R:Any> handlerForQuery(query: Q) =
        (queryHandlers as List<QueryHandler<Q, R>>)
            .first { handler -> query::class == handler.queryType }
}
interface Query<Aggregate : Any> {
    val id: AggregateId
}
@Transactional(transactionManager = "rdbms-tx-mgr")
interface QueryHandler<Q: Query<Representation>, Representation: Any> {
    val queryType: KClass<Q>
    fun handle(query: Q): Representation
}





typealias DomainEventId = UUID
abstract class DomainEvent(private val id: DomainEventId = UUID.randomUUID()) {
    override fun toString(): String {
        return this::javaClass.name
    }
}

@Component
class DomainEventBroadcaster {
    private val logger = LoggerFactory.getLogger(DomainEventBroadcaster::class.java)

    private val events: MutableList<DomainEvent> = mutableListOf()

    fun publish(domainEvent: DomainEvent) {
        events += domainEvent
        logger.info("$domainEvent was broadcast")
    }

    fun <T> DomainEventBroadcaster.findEvent(clazz: Class<T>): T? {
        return events.filterIsInstance(clazz).firstOrNull()
    }
}

