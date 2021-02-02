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
    private inline fun <reified Cmd: Command> handlerForCommand(command: Cmd) =
        (commandHandlers as List<CommandHandler<Cmd>>)
            .first { handler -> command::class.java == handler.commandType }
}
@Component
interface CommandHandler<Cmd: Command> {
    fun handle(cmd: Cmd): DomainEvent
}
inline val <reified Cmd: Command> CommandHandler<Cmd>.commandType get() = Cmd::class.java




interface Query<Aggregate : Any> {
    val id: AggregateId
}
@Component
class QueryExecutor(
    private val queryHandlers: List<QueryHandler<*,*>>
) {
    fun <Agg : Any, R> execute(query: Query<Agg>, transformer: (Agg) -> R): R? {
        return handlerForQuery(query).handle(query)?.let { transformer(it) }
    }
    private inline fun <reified Q: Query<R>, R:Any> handlerForQuery(query: Q) =
        (queryHandlers as List<QueryHandler<Q, R>>)
            .first { handler -> query::class.java == handler.queryType }
}
@Component
interface QueryHandler<Q: Query<Representation>, Representation: Any> {
    fun handle(query: Q): Representation?
}
inline val <reified Q: Query<*>> QueryHandler<Q, *>.queryType get() = Q::class.java



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

