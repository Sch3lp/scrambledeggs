package org.scrambled.scenariotests.scenarios

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.scrambled.infra.cqrs.DomainEventBroadcaster
import org.scrambled.scenariotests.steps.registerPlayerStep
import org.scrambled.scenariotests.steps.verifyRegisteredPlayer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RegistrationScenario {

    @Autowired
    private lateinit var broadcaster: DomainEventBroadcaster

    @Test
    fun `An anonymous user registers themselves and becomes a Registered Player`() {
        val playerNickname = "Sch3lp"
        runBlocking { registerPlayerStep(playerNickname) }
        with(broadcaster) {
            verifyRegisteredPlayer(playerNickname)
        }
    }
}