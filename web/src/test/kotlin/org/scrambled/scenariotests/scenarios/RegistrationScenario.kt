package org.scrambled.scenariotests.scenarios

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.filterEvents
import org.scrambled.adapter.eventsourcing.eventstore.PostgresEventStore
import org.scrambled.adapter.restapi.leaderboards.LeaderboardEntryJson
import org.scrambled.scenariotests.steps.core.fetchPlayerStep
import org.scrambled.scenariotests.steps.core.registerPlayerStep
import org.scrambled.scenariotests.steps.leaderboard.fetchLeaderboardStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RegistrationScenario {

    @Autowired
    private lateinit var eventStream: PostgresEventStore

    @Autowired
    private lateinit var jdbi: Jdbi
    @Autowired
    private lateinit var client: DatabaseClient

    @BeforeEach
    internal fun setUp() {
        wipeDatabases()
    }

    @Test
    fun `An anonymous user registers themselves and becomes a Registered Player, and a Leaderboard is regenerated with them in it`() {
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
        Thread.sleep(1000L)
        runBlocking {
            val leaderboard: List<LeaderboardEntryJson> = fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(LeaderboardEntryJson(rank = null, nickname = "Sch3lp", score = 0))
        }
    }

    private fun wipeDatabases() {
        val handle = jdbi.open()
        handle.execute(
            """
                delete from registered_players where 1=1;
                delete from most_challenges_done_leaderboard where 1=1;
                  """
        )

        runBlocking { client.sql { "delete from eventstore where 1=1" }.await() }
    }
}
