package org.scrambled.adapter.rdbms.koin

import org.koin.dsl.module
import org.scrambled.adapter.rdbms.JdbiConfig
import org.scrambled.domain.core.api.players.QueryablePlayers

fun rdbmsModule() = module(createdAtStart = true) {
    val jdbiConfig = JdbiConfig()

    single { jdbiConfig.driverManagerDataSource() }
    single { jdbiConfig.jdbi(get())}
    single<QueryablePlayers> { jdbiConfig.playersDao(get())}
}