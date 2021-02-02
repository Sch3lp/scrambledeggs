package org.scrambled.infra.cqrs

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

typealias AggregateId = UUID

interface Command {
    val id: AggregateId
}
@Component
class CommandExecutor(
    private val commandHandlers: List<CommandHandler<*>>,
    private val domainEventBroadcaster: DomainEventBroadcaster
) {
    fun execute(command: Command) {
        val domainEvent = handlerForCommand(command).handle(command)
        domainEventBroadcaster.publish(domainEvent)
    }
    private fun <Cmd: Command> handlerForCommand(command: Cmd) =
        (commandHandlers as List<CommandHandler<Cmd>>)
            .first { it.canHandle(command::class.java) }
}
@Component
interface CommandHandler<Cmd> {
    fun handle(cmd: Cmd): DomainEvent
    fun canHandle(commandType: Class<out Cmd>): Boolean
}




interface Query<Aggregate : Any> {
    val id: AggregateId
}
@Component
class QueryExecutor(
    private val queryHandlers: List<QueryHandler<*,*>>
) {
    fun <Agg : Any, R> execute(query: Query<Agg>, transformer: (Agg) -> R): R? {
        return handlerForQuery<Query<Agg>, Agg>().handle(query)?.let { transformer(it) }
    }
    private fun <Q: Query<R>, R:Any> handlerForQuery() =
        (queryHandlers as List<QueryHandler<Q, R>>).first()
}
@Component
interface QueryHandler<Q: Query<Representation>, Representation: Any> {
    fun handle(query: Q): Representation?
}



@Component
interface Repository<Aggregate> {
    fun getById(id: AggregateId): Aggregate?
    fun save(registeredPlayer: Aggregate)
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

