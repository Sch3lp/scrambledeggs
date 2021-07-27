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
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.scenariotests.steps.client.createClient
import org.scrambled.scenariotests.steps.core.*
import org.scrambled.scenariotests.steps.leaderboard.fetchLeaderboardStep
import org.scrambled.scenariotests.steps.leaderboard.triggerLeaderboardRehydration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.await

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ChallengingScenario {

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
    fun `A registered player challenges another player and has their score increased`() {
        val sch3lpsJwtInfo = JwtInfo("http://google.com", "schlep")
        var sch3lpPlayerId: PlayerId
        val rgm3JwtInfo = JwtInfo("http://google.com", "monsieurRgm")
        var rgm3PlayerId: PlayerId
        val schlepsClient = createClient()
        val rgmsClient = createClient()
        runBlocking{
            schlepsClient.exchangeCookie(sch3lpsJwtInfo)
            sch3lpPlayerId = schlepsClient.registerPlayerStep("Sch3lp").expectSuccess()

            rgmsClient.exchangeCookie(rgm3JwtInfo)
            rgm3PlayerId = rgmsClient.registerPlayerStep("rgm3").expectSuccess()

            schlepsClient.challengePlayerStep(sch3lpPlayerId, rgm3PlayerId).expectSuccess()
        }

        runBlocking {
            val firstPlayerChallenged = eventStream.filterEvents<Event.PlayerChallenged>().first()
            assertThat(firstPlayerChallenged.challenger).isEqualTo(sch3lpPlayerId)
            assertThat(firstPlayerChallenged.opponent).isEqualTo(rgm3PlayerId)
        }
        runBlocking { schlepsClient.triggerLeaderboardRehydration() }
        runBlocking {
            val leaderboard: List<LeaderboardEntryJson> = schlepsClient.fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(
                LeaderboardEntryJson(rank = 1, nickname = "Sch3lp", score = 1),
                LeaderboardEntryJson(rank = null, nickname = "rgm3", score = 0),
            )
        }
    }

    private fun wipeDatabases() {
        println("🚨Wiping database🚨")
        val handle = jdbi.open()
        handle.execute(
            """
                delete from registered_players;
                delete from most_challenges_done_leaderboard;
                  """
        )

        runBlocking { client.sql { "delete from eventstore where 1=1" }.await() }
    }
}
