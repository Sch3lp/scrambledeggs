package org.scrambled

import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger
import org.scrambled.adapter.rdbms.koin.rdbmsModule
import org.scrambled.core.impl.players.FetchAllRegisteredPlayersQueryHandler
import org.scrambled.core.impl.players.RegisterPlayerHandler
import org.scrambled.core.impl.players.RegisteredPlayerRepository
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.QueryablePlayer
import org.scrambled.domain.core.api.players.QueryablePlayers
import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import org.scrambled.infra.domainevents.DomainEvent
import org.scrambled.infra.domainevents.IDomainEventBroadcaster
import org.scrambled.ktorapi.koin.ktorApi
import org.slf4j.LoggerFactory

fun main() {
    embeddedServer(
        factory = Netty,
        port = 8080,
        module = createScrambledKtorModule(
//            inMemDbModule(),
            rdbmsModule(),
            domainModule(),
            infraModule(),
        )
    ).start(wait = true)
}

fun createScrambledKtorModule(vararg koinModules: Module): Application.() -> Unit {

    fun Application.main() {
        install(Koin) {
            slf4jLogger()
            modules(*koinModules)
        }

        ktorApi()

    }
    return Application::main
}

fun inMemDbModule() = module(createdAtStart = true) {
    single<QueryablePlayers> {
        PlayersInMem()
    }
}

fun domainModule(): Module = module(createdAtStart = true) {
    singleBy<RegisterPlayerHandler,RegisterPlayerHandler>()
    singleBy<FetchAllRegisteredPlayersQueryHandler,FetchAllRegisteredPlayersQueryHandler>()
    singleBy<RegisteredPlayerRepository,RegisteredPlayerRepository>()
}

fun infraModule() = module(createdAtStart = true) {
    single {
        CommandExecutor(listOf(get<RegisterPlayerHandler>()), get())
    }
    single {
        QueryExecutor(listOf(get<FetchAllRegisteredPlayersQueryHandler>()))
    }
    singleBy<IDomainEventBroadcaster, DomainEventsInMem>()
}


class PlayersInMem(private val _players: MutableList<QueryablePlayer> = mutableListOf())
    : QueryablePlayers {

    override fun getById(id: PlayerId): QueryablePlayer?
        = _players.find { it.id == id }

    override fun store(player: QueryablePlayer) {
        _players.add(player)
    }

    override fun all(): List<QueryablePlayer>
        = _players

}

class DomainEventsInMem : IDomainEventBroadcaster {

    private val logger = LoggerFactory.getLogger(DomainEventsInMem::class.java)

    private val events: MutableList<DomainEvent> = mutableListOf()

    override fun publish(domainEvent: DomainEvent) {
        events += domainEvent
        logger.info("$domainEvent was broadcast")
    }
    fun <T> findEvent(clazz: Class<T>): T? {
        return events.filterIsInstance(clazz).firstOrNull()
    }
}
