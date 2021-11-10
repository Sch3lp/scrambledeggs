package org.scrambled.adapter.eventsourcing

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.AutoConfigurationPackage
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootConfiguration
@AutoConfigurationPackage
@ComponentScan
class PostgresEventStoreTestConfig {

    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer<PostgreSQLContainer<*>>("postgres:13.1-alpine").apply {
            withExposedPorts(5432)
            withDatabaseName("postgreseventsdb")
            withUsername("snarf")
            withPassword("Lion-0!")
            withInitScript("db/migrations/V1__CreateEventStoreTable.sql")
            start()
        }
    }

    @Primary
    @Bean
    fun postgresProps(postgres: PostgreSQLContainer<*>): R2dbcProperties {
        return R2dbcProperties().apply {
            username = postgres.username
            password = postgres.password
            name = postgres.databaseName
            url = "r2dbc:postgres://localhost:${postgres.firstMappedPort}/${postgres.databaseName}"
        }
    }
}