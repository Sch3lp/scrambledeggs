package org.scrambled.adapter.rdbms

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import org.jdbi.v3.sqlobject.kotlin.KotlinSqlObjectPlugin
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.scrambled.adapter.rdbms.core.challenges.ChallengesDao
import org.scrambled.adapter.rdbms.core.players.PlayersDao
import org.scrambled.adapter.rdbms.leaderboard.projection.MostChallengesDoneDao
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource
import org.springframework.jdbc.datasource.DataSourceTransactionManager

import org.springframework.jdbc.datasource.DriverManagerDataSource


@Configuration
class JdbiConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    fun driverManagerDataSource(): DataSource {
        return DriverManagerDataSource()
    }

    @Bean
    @Qualifier("rdbms-tx-mgr")
    fun dataSourceTransactionManager(dataSource: DataSource): DataSourceTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }

    @Bean
    fun jdbi(ds: DataSource): Jdbi {
        val proxy = TransactionAwareDataSourceProxy(ds)
        val jdbi: Jdbi = Jdbi.create(proxy)
        return with(jdbi) {
            installPlugin(KotlinPlugin())
            installPlugin(KotlinSqlObjectPlugin())
            installPlugin(PostgresPlugin())
        }
    }

    @Bean
    fun playersDao(jdbi: Jdbi): PlayersDao = jdbi.onDemand()

    @Bean
    fun challengesDao(jdbi: Jdbi): ChallengesDao = jdbi.onDemand()

    @Bean
    fun mostChallengesDoneDao(jdbi: Jdbi): MostChallengesDoneDao = jdbi.onDemand()
}