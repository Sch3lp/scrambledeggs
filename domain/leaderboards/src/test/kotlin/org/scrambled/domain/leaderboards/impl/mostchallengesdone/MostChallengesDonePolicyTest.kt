package org.scrambled.domain.leaderboards.impl.mostchallengesdone

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardRepository
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.ProjectedPlayer
import java.util.*

@ExtendWith(value = [MockitoExtension::class])
internal class MostChallengesDonePolicyTest {

    lateinit var broadcastEvents: BroadcastEvents

    @Mock
    lateinit var mostChallengesDoneLeaderboardRepository: MostChallengesDoneLeaderboardRepository

    lateinit var policy: MostChallengesDonePolicy

    @BeforeEach
    internal fun setUp() {
        //TODO: move InMemoryBroadcastEvents to proper "test-fixtures" config of leaderboards.api
        //see https://stackoverflow.com/questions/5644011/multi-project-test-dependencies-with-gradle/60138176#60138176
        broadcastEvents = InMemoryBroadcastEvents()
        policy = MostChallengesDonePolicy(broadcastEvents, mostChallengesDoneLeaderboardRepository)
    }

    @Test
    internal fun `when broadcastEvents are digested, then a new MostChallengesDoneLeaderboard projection is saved`() {
        broadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(UUID.randomUUID(), "Sch3lp"))

        policy.nom()

        val expectedProjection =
            MostChallengesDoneLeaderboardProjection(listOf(ProjectedPlayer(nickname = "Sch3lp", score = 0)))

        verify(mostChallengesDoneLeaderboardRepository).save(expectedProjection)
    }
}