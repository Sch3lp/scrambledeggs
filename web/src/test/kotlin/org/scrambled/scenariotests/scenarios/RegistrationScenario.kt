package org.scrambled.scenariotests.scenarios

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.filterEvents
import org.scrambled.adapter.eventsourcing.eventstore.PostgresEventStore
import org.scrambled.scenariotests.steps.fetchPlayerStep
import org.scrambled.scenariotests.steps.registerPlayerStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RegistrationScenario {

    @Autowired
    private lateinit var eventStream: PostgresEventStore

    @Test
    fun `An anonymous user registers themselves and becomes a Registered Player`() {
        val playerNickname = "Sch3lp"
        runBlocking {
            val playerId = registerPlayerStep(playerNickname)
            val registeredPlayer = fetchPlayerStep(playerId)
            assertThat(registeredPlayer.nickname).isEqualTo("Sch3lp")
        }
        runBlocking {
            val firstPlayerRegistered = eventStream.filterEvents<Event.PlayerRegistered>().first()
            assertThat(firstPlayerRegistered.nickname).isEqualTo("Sch3lp")
        }
        /*
        verifyMostChallengesDoneLeaderboard(dataSource) {
            rank 1 for "Sch3lp" with 0 points
        }
         */

    }
}
