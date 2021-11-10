package org.scrambled.leaderboards.adapters.out.rdbms

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.scrambled.leaderboards.adapters.out.rdbms.projection.MostChallengesDoneDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class LeaderboardsJdbiConfig {
    @Bean
    fun mostChallengesDoneDao(jdbi: Jdbi): MostChallengesDoneDao = jdbi.onDemand()
}