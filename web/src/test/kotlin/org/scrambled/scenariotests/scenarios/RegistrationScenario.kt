package org.scrambled.scenariotests.scenarios

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.scrambled.infra.cqrs.IDomainEventBroadcaster
import org.scrambled.infra.cqrs.InMemoryDomainEventBroadcaster
import org.scrambled.scenariotests.steps.fetchPlayerStep
import org.scrambled.scenariotests.steps.registerPlayerStep
import org.scrambled.scenariotests.steps.verifyRegisteredPlayer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = [InMemBroadcasterConfig::class])
class RegistrationScenario {

    @Autowired
    private lateinit var broadcaster: IDomainEventBroadcaster

    @Test
    fun `An anonymous user registers themselves and becomes a Registered Player`() {
        val playerNickname = "Sch3lp"
        runBlocking {
            val playerId = registerPlayerStep(playerNickname)
            val registeredPlayer = fetchPlayerStep(playerId)
            assertThat(registeredPlayer.nickname).isEqualTo("Sch3lp")
        }
        with(broadcaster as InMemoryDomainEventBroadcaster) {
            verifyRegisteredPlayer(playerNickname)
        }
        //TODO expand test to verify correct events in eventstore
    }
}

@TestConfiguration
class InMemBroadcasterConfig {
    @Bean
    @Primary
    fun broadcaster(): IDomainEventBroadcaster = InMemoryDomainEventBroadcaster()
}