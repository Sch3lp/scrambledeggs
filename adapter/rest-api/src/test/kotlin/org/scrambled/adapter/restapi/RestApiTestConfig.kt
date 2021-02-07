package org.scrambled.adapter.restapi

import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.lang.Exception

@ComponentScan
@Configuration
class RestApiTestConfig {

    @MockBean
    lateinit var commandExecutor: CommandExecutor
    @MockBean
    lateinit var queryExecutor: QueryExecutor
}
