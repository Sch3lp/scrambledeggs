package org.scrambled.leaderboards.domain.core.mostchallengesdone

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvent
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvents
import org.scrambled.leaderboards.domain.api.infra.InMemoryBroadcastEvents
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.ProjectedPlayer
import java.util.*

@ExtendWith(value = [MockitoExtension::class])
internal class MostChallengesDonePolicyTest {

    lateinit var broadcastEvents: BroadcastEvents

    @Mock
    lateinit var mostChallengesDoneProjections: MostChallengesDoneLeaderboardProjection

    lateinit var policy: MostChallengesDonePolicy

    @BeforeEach
    internal fun setUp() {
        broadcastEvents = InMemoryBroadcastEvents()
        policy = MostChallengesDonePolicy(broadcastEvents, mostChallengesDoneProjections)
    }

    @Test
    internal fun `when broadcastEvents are digested, then a completely new MostChallengesDoneLeaderboard projection is regenerated`() {
        val playerId = UUID.randomUUID()
        broadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(playerId, "Sch3lp"))

        policy.regenerateLeaderboard()

        val inOrder = Mockito.inOrder(mostChallengesDoneProjections)
        inOrder.verify(mostChallengesDoneProjections).wipe()
        inOrder.verify(mostChallengesDoneProjections).store(listOf(ProjectedPlayer(nickname = "Sch3lp", score = 0, playerId = playerId)))
    }
}