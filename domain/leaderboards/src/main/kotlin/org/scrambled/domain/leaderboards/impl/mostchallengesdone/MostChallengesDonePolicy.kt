package org.scrambled.domain.leaderboards.impl.mostchallengesdone

import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.MostChallengesDoneLeaderboardProjection
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.ProjectedPlayer
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MostChallengesDonePolicy(
    private val broadcastEvents: BroadcastEvents,
    private val mostChallengesDoneProjection: MostChallengesDoneLeaderboardProjection //which is really just a stupid normalized database table with a ranking
) {

    private val logger = LoggerFactory.getLogger(MostChallengesDonePolicy::class.java)

//    @Scheduled(fixedDelay = 1000 * 60 * 5) // Every 5 minutes
    @Scheduled(fixedDelay = 500)
    fun regenerateLeaderboard() {
        val events: List<BroadcastEvent> = broadcastEvents.findAll()

        MostChallengesDoneLeaderboard.rehydrate(events)
            .project()
            .regenerate()
    }

    fun List<ProjectedPlayer>.regenerate() {
        logger.debug("Regenerate triggered with $size players")
        mostChallengesDoneProjection.wipe()
        mostChallengesDoneProjection.store(this)
    }
}

class MostChallengesDoneLeaderboard private constructor(private val events: List<BroadcastEvent>) {

//    private val scoringAlgorithm: (events: List<BroadcastEvent>) -> Score = { _ -> 0 }

    private val players: List<String>
        get() = events.filterIsInstance<BroadcastEvent.PlayerRegisteredForLeaderboard>()
            .map { it.nickname }

    fun project(): List<ProjectedPlayer> {
        return players.map { ProjectedPlayer(nickname = it, score = 0) }
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