package org.scrambled.adapter.rdbms.core

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.scrambled.adapter.rdbms.core.challenges.ChallengesDao
import org.scrambled.adapter.rdbms.core.players.PlayersDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MatchesJdbiConfig {
    @Bean
    fun playersDao(jdbi: Jdbi): PlayersDao = jdbi.onDemand()

    @Bean
    fun challengesDao(jdbi: Jdbi): ChallengesDao = jdbi.onDemand()

}