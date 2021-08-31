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
import org.scrambled.adapter.restapi.JwtInfo
import org.scrambled.adapter.restapi.leaderboards.LeaderboardEntryJson
import org.scrambled.scenariotests.steps.client.createClient
import org.scrambled.scenariotests.steps.core.*
import org.scrambled.scenariotests.steps.leaderboard.fetchLeaderboardStep
import org.scrambled.scenariotests.steps.leaderboard.triggerLeaderboardRehydration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await
import org.springframework.test.context.ActiveProfiles

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
        val jwtInfo = JwtInfo("http://google.com", "schlep")
        val client = createClient(jwtInfo)
        runBlocking {
            val playerId = client.registerPlayerStep(playerNickname).expectSuccess()
            val registeredPlayer = client.fetchPlayerStep(playerId)
            assertThat(registeredPlayer.nickname).isEqualTo("Sch3lp")
            val registeredPlayers = client.fetchAllPlayersStep()
            assertThat(registeredPlayers)
                .extracting<String> { it.nickname }
                .containsExactly("Sch3lp")
        }
        runBlocking {
            val firstPlayerRegistered = eventStream.filterEvents<Event.PlayerRegistered>().first()
            assertThat(firstPlayerRegistered.nickname).isEqualTo("Sch3lp")
        }
        runBlocking { client.triggerLeaderboardRehydration() }
        runBlocking {
            val leaderboard: List<LeaderboardEntryJson> = client.fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(LeaderboardEntryJson(rank = null, nickname = "Sch3lp", score = 0))
        }
        runBlocking {
            val registeredPlayer = client.fetchPlayerByJwtInfoStep()
            assertThat(registeredPlayer?.nickname).isEqualTo("Sch3lp")
        }
    }

    @Test
    fun `An anonymous user registers themselves twice with the same external account ref and receives an error`() {
        val jwtInfo = JwtInfo("http://google.com", "coreDusk")
        val client = createClient(jwtInfo)
        val playerNickname = "CoredusK"
        runBlocking {
            val playerId = client.registerPlayerStep(playerNickname).expectSuccess()
            val registeredPlayer = client.fetchPlayerStep(playerId)
            assertThat(registeredPlayer.nickname).isEqualTo("CoredusK")
            val errorMessage = client.registerPlayerStep(playerNickname).expectFailure()
            assertThat(errorMessage).isEqualTo("You can only register once with the same Epic account")
        }
        runBlocking {
            val firstPlayerRegistered = eventStream.filterEvents<Event.PlayerRegistered>().first()
            assertThat(firstPlayerRegistered.nickname).isEqualTo("CoredusK")
        }

        runBlocking { client.triggerLeaderboardRehydration() }

        runBlocking {
            val leaderboard: List<LeaderboardEntryJson> = client.fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(LeaderboardEntryJson(rank = null, nickname = "CoredusK", score = 0))
        }
    }

    private fun wipeDatabases() {
        println("🚨Wiping database🚨")
        val handle = jdbi.open()
        handle.execute(
            """
                delete from challenges;
                delete from registered_players;
                delete from most_challenges_done_leaderboard;
                  """
        )

        runBlocking { client.sql { "delete from eventstore where 1=1" }.await() }
    }
}
