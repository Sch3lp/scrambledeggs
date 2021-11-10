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
import org.scrambled.matches.adapters.out.rdbms.challenges.ChallengesDao
import org.scrambled.matches.adapters.`in`.rest.challenging.PendingChallengeJson
import org.scrambled.leaderboards.adapters.`in`.rest.leaderboards.LeaderboardEntryJson
import org.scrambled.matches.domain.api.challenges.GameMode
import org.scrambled.matches.domain.api.challenges.PlayerId
import org.scrambled.scenariotests.steps.client.createClient
import org.scrambled.scenariotests.steps.core.acceptChallengeStep
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
        runBlocking {
            val challengerClient = createClient("schlep")
            val opponentClient = createClient("monsieurRgm")
            val sch3lpPlayerId: PlayerId = challengerClient.registerPlayerStep("Sch3lp").expectSuccess()
            val rgm3PlayerId: PlayerId = opponentClient.registerPlayerStep("rgm3").expectSuccess()

            val comment = "Some comment"
            val suggestion = "Next wednesday at 20:00"
            val gameMode = GameMode.Duel

            challengerClient.challengePlayerStep(sch3lpPlayerId, rgm3PlayerId, comment, suggestion, gameMode)
                .expectSuccess()

            val challenge = challengeDao.findChallenge(sch3lpPlayerId, rgm3PlayerId).last()
            assertThat(challenge.comment).isEqualTo(comment)
            assertThat(challenge.appointmentSuggestion).isEqualTo(suggestion)
            assertThat(challenge.gameMode).isEqualTo(gameMode)

            val firstPlayerChallenged = eventStream.filterEvents<Event.PlayerChallenged>().first()
            assertThat(firstPlayerChallenged.challenger).isEqualTo(sch3lpPlayerId)
            assertThat(firstPlayerChallenged.opponent).isEqualTo(rgm3PlayerId)
            challengerClient.triggerLeaderboardRehydration()

            val leaderboard: List<LeaderboardEntryJson> = challengerClient.fetchLeaderboardStep()
            assertThat(leaderboard).containsExactly(
                LeaderboardEntryJson(rank = 1, nickname = "Sch3lp", score = 1, playerId = sch3lpPlayerId),
                LeaderboardEntryJson(rank = null, nickname = "rgm3", score = 0, playerId = rgm3PlayerId),
            )

            val pendingChallenges: List<PendingChallengeJson> = opponentClient.fetchPendingChallengesStep()
            assertThat(pendingChallenges).containsExactly(
                PendingChallengeJson(challenge.challengeId, gameMode, "Sch3lp", suggestion, comment)
            )

            opponentClient.acceptChallengeStep(challenge.challengeId)
            assertThat(challengeDao.getByChallengeId(challenge.challengeId)?.isAccepted).isTrue()
            assertThat(opponentClient.fetchPendingChallengesStep()).isEmpty()
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
