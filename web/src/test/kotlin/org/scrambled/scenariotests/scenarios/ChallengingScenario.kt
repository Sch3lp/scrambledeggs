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
import org.scrambled.adapter.rdbms.core.challenges.ChallengesDao
import org.scrambled.adapter.restapi.JwtInfo
import org.scrambled.adapter.restapi.challenging.PendingChallengeJson
import org.scrambled.adapter.restapi.leaderboards.LeaderboardEntryJson
import org.scrambled.domain.core.api.challenges.GameMode
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.scenariotests.steps.client.createClient
import org.scrambled.scenariotests.steps.core.challengePlayerStep
import org.scrambled.scenariotests.steps.core.fetchPendingChallengesStep
import org.scrambled.scenariotests.steps.core.registerPlayerStep
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

    @Autowired
    private lateinit var challengeDao: ChallengesDao

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
        val challengerClient = createClient(sch3lpsJwtInfo)
        val opponentClient = createClient(rgm3JwtInfo)
        val comment = "Some comment"
        val suggestion = "Next wednesday at 20:00"
        val gameMode = GameMode.Duel
        runBlocking{
            sch3lpPlayerId = challengerClient.registerPlayerStep("Sch3lp").expectSuccess()

            rgm3PlayerId = opponentClient.registerPlayerStep("rgm3").expectSuccess()

            challengerClient.challengePlayerStep(sch3lpPlayerId, rgm3PlayerId, comment, suggestion, gameMode).expectSuccess()
        }

        val challenge = challengeDao.findChallenge(sch3lpPlayerId, rgm3PlayerId).last()
        assertThat(challenge.comment).isEqualTo(comment)
        assertThat(challenge.appointmentSuggestion).isEqualTo(suggestion)
        assertThat(challenge.gameMode).isEqualTo(gameMode)

        runBlocking {
            val firstPlayerChallenged = eventStream.filterEvents<Event.PlayerChallenged>().first()
            assertThat(firstPlayerChallenged.challenger).isEqualTo(sch3lpPlayerId)
            assertThat(firstPlayerChallenged.opponent).isEqualTo(rgm3PlayerId)
        }
        runBlocking { challengerClient.triggerLeaderboardRehydration() }
        runBlocking {
            val leaderboard: List<LeaderboardEntryJson> = challengerClient.fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(
                LeaderboardEntryJson(rank = 1, nickname = "Sch3lp", score = 1, playerId = sch3lpPlayerId),
                LeaderboardEntryJson(rank = null, nickname = "rgm3", score = 0, playerId = rgm3PlayerId),
            )
        }

        runBlocking {
            val pendingChallenges: List<PendingChallengeJson> = opponentClient.fetchPendingChallengesStep()
            assertThat(pendingChallenges).containsExactly(
                PendingChallengeJson(challenge.id, gameMode, "Sch3lp", suggestion)
            )
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
