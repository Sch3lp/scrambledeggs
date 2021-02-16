package org.scrambled.domain.leaderboards.impl.mostchallengesdone

import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardRepository
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.ProjectedPlayer
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.Score
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MostChallengesDonePolicy(
    private val broadcastEvents: BroadcastEvents,
    private val mostChallengesDoneLeaderboardRepository: MostChallengesDoneLeaderboardRepository //which is really just a stupid normalized database table with a ranking
) {

    @Scheduled(cron = "* * * * 5")
    fun nom() {
        val events: List<BroadcastEvent> = broadcastEvents.findAll()

        MostChallengesDoneLeaderboard.rehydrate(events)
            .project()
            .save()
    }

    fun MostChallengesDoneLeaderboardProjection.save() {
        mostChallengesDoneLeaderboardRepository.save(this)
    }
}

class MostChallengesDoneLeaderboard private constructor(private val events: List<BroadcastEvent>) {

//    private val scoringAlgorithm: (events: List<BroadcastEvent>) -> Score = { _ -> 0 }

    private val players: List<String>
        get() = events.filterIsInstance<BroadcastEvent.PlayerRegisteredForLeaderboard>()
            .map { it.nickname }

    fun project(): MostChallengesDoneLeaderboardProjection {
        return MostChallengesDoneLeaderboardProjection(players.map { ProjectedPlayer(nickname = it, score = 0) })
    }

    companion object {
        /**
         * Applies the MostChallengesDoneLeaderboard's scoring algorithm in order to "recover" this kind of leaderboard's state
         * which we can then project into some normalized format to save in the database
         */
        fun rehydrate(events: List<BroadcastEvent>): MostChallengesDoneLeaderboard {
            return MostChallengesDoneLeaderboard(events)
        }
    }
}