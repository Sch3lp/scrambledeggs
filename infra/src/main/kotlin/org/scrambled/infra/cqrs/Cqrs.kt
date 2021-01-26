package org.scrambled.infra.cqrs

import java.util.*

typealias AggregateId = UUID

interface Command {
    val id: AggregateId
}
class CommandExecutor(
    private val commandHandlers: List<CommandHandler<*>>
) {
    fun <Cmd : Command> execute(command: Cmd) {
        handlerForCommand<Cmd>().handle(command)
    }
    private fun <Cmd: Command> handlerForCommand() =
        (commandHandlers as List<CommandHandler<Cmd>>).first()
}
interface CommandHandler<Cmd> {
    fun handle(cmd: Cmd): DomainEvent
}


interface Query<Aggregate : Any> {
    val id: AggregateId
}
class QueryExecutor(
    private val queryHandlers: List<QueryHandler<*,*>>
) {
    fun <Agg : Any, R> execute(query: Query<Agg>, transformer: (Agg) -> R): R? {
        return handlerForQuery<Query<Agg>, Agg>().handle(query)?.let { transformer(it) }
    }
    private fun <Q: Query<R>, R:Any> handlerForQuery() =
        (queryHandlers as List<QueryHandler<Q, R>>).first()
}
interface QueryHandler<Q: Query<Representation>, Representation: Any> {
    fun handle(query: Q): Representation?
}




interface Repository<Aggregate> {
    fun getById(id: AggregateId): Aggregate?
}
fun <Agg : Any> repositoryForAggregate(): Repository<Agg> {
    val repositories: List<Repository<Agg>> = listOf()
    return repositories.first()
}


typealias DomainEventId = UUID
abstract class DomainEvent(private val id: DomainEventId = UUID.randomUUID())
