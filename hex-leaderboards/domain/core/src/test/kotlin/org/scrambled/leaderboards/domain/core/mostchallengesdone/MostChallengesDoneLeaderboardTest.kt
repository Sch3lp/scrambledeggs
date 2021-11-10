package org.scrambled.leaderboards.domain.core.mostchallengesdone

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvent
import org.scrambled.leaderboards.domain.api.infra.InMemoryBroadcastEvents
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.ProjectedPlayer
import java.util.*


class MostChallengesDoneLeaderboardTest {

    @Test
    internal fun `MostChallengesDoneLeaderboard creates a ranking where the player that sent out the most challenges has the highest rank`() {
        val sch3lpId = UUID.randomUUID()
        val decripId = UUID.randomUUID()
        val inMemBroadcastEvents = InMemoryBroadcastEvents()
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(sch3lpId, "Sch3lp"))
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(decripId, "Decrip"))
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(sch3lpId, decripId))
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(sch3lpId, decripId))
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(decripId, sch3lpId))

        val ranking = MostChallengesDoneLeaderboard.rehydrate(inMemBroadcastEvents).project()

        assertThat(ranking)
            .containsExactly(
                ProjectedPlayer(1, "Sch3lp", 2, sch3lpId),
                ProjectedPlayer(2, "Decrip", 1, decripId)
            )
    }

    @Test
    internal fun `MostChallengesDoneLeaderboard's rank remains null when a player has not sent out any challenges`() {
        val sch3lpId = UUID.randomUUID()
        val decripId = UUID.randomUUID()
        val inMemBroadcastEvents = InMemoryBroadcastEvents()
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(sch3lpId, "Sch3lp"))
        inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(decripId, "Decrip"))

        val ranking = MostChallengesDoneLeaderboard.rehydrate(inMemBroadcastEvents).project()

        assertThat(ranking)
            .containsExactly(
                ProjectedPlayer(null, "Sch3lp", 0, sch3lpId),
                ProjectedPlayer(null, "Decrip", 0, decripId)
            )
    }

    @Nested
    inner class ScoringAlgo {
        @Test
        internal fun `MostChallengesAlgorithm attributes 1 point per challenge initiated`() {
            val sch3lpId = UUID.randomUUID()
            val decripId = UUID.randomUUID()
            val inMemBroadcastEvents = InMemoryBroadcastEvents()
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(sch3lpId, decripId))
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(sch3lpId, decripId))
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(decripId, sch3lpId))

            val sch3lpsScore = mostChallengesDoneAlgorithm(inMemBroadcastEvents, sch3lpId)
            val decripsScore = mostChallengesDoneAlgorithm(inMemBroadcastEvents, decripId)

            assertThat(sch3lpsScore).isEqualTo(2)
            assertThat(decripsScore).isEqualTo(1)
        }


        @Test
        internal fun `MostChallengesAlgorithm without any PlayerChallenged events, returns score of 0`() {
            val sch3lpId = UUID.randomUUID()
            val decripId = UUID.randomUUID()
            val inMemBroadcastEvents = InMemoryBroadcastEvents()
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(sch3lpId, "Sch3lp"))
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerRegisteredForLeaderboard(decripId, "Decrip"))
            inMemBroadcastEvents.keep(BroadcastEvent.PlayerChallengedForLeaderboard(decripId, sch3lpId))

            val sch3lpsScore = mostChallengesDoneAlgorithm(inMemBroadcastEvents, sch3lpId)

            assertThat(sch3lpsScore).isEqualTo(0)
        }

        @Test
        internal fun `MostChallengesAlgorithm without any events, returns score of 0`() {
            val inMemBroadcastEvents = InMemoryBroadcastEvents()

            val somebodysScore = mostChallengesDoneAlgorithm(inMemBroadcastEvents, UUID.randomUUID())

            assertThat(somebodysScore).isEqualTo(0)
        }
    }

}