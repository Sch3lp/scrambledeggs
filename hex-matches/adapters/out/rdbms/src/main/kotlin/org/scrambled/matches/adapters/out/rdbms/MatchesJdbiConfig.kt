package org.scrambled.matches.adapters.out.rdbms

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.scrambled.matches.adapters.out.rdbms.challenges.ChallengesDao
import org.scrambled.matches.adapters.out.rdbms.players.PlayersDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MatchesJdbiConfig {
    @Bean
    fun playersDao(jdbi: Jdbi): PlayersDao = jdbi.onDemand()

    @Bean
    fun challengesDao(jdbi: Jdbi): ChallengesDao = jdbi.onDemand()

}