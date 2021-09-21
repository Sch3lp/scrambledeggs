package org.scrambled.domain.leaderboards.impl.mostchallengesdone

import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents
import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.*
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.domainevents.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*

internal typealias PlayerId = UUID

internal fun mostChallengesDoneAlgorithm(events: List<BroadcastEvent>, playerId: PlayerId): Score {
    return events
        .filterEvents<BroadcastEvent.PlayerChallengedForLeaderboard>()
        .count { it.challenger == playerId }
}

internal inline fun <reified T : BroadcastEvent> List<BroadcastEvent>.filterEvents() = filterIsInstance<T>()


data class ProjectedPlayerPartial(
    val playerId: PlayerId,
    val nickname: Nickname,
    val score: Score? = null,
    val rank: Rank? = null,
)


class MostChallengesDoneLeaderboard private constructor(private val events: List<BroadcastEvent>) {

    private val scoringAlgorithm: (events: List<BroadcastEvent>, playerId: PlayerId) -> Score =
        ::mostChallengesDoneAlgorithm

    private val players: List<ProjectedPlayerPartial>
        get() = events.filterIsInstance<BroadcastEvent.PlayerRegisteredForLeaderboard>()
            .map { ProjectedPlayerPartial(it.playerId, it.nickname) }

    fun project() = players
        .score()
        .rank()
        .build()

    private fun ProjectedPlayerPartial.score() = this.copy(score = scoringAlgorithm(events, this.playerId))

    private fun List<ProjectedPlayerPartial>.score() = this
        .map { partial -> partial.score() }

    private fun List<ProjectedPlayerPartial>.rank() = this
        .sortedByDescending { it.score }
        .mapIndexed { index, projectedPlayer ->
            if (projectedPlayer.score == 0) {
                projectedPlayer
            } else {
                projectedPlayer.copy(rank = index + 1)
            }
        }

    private fun List<ProjectedPlayerPartial>.build() = this.map { it.build() }
    private fun ProjectedPlayerPartial.build(): ProjectedPlayer =
        ProjectedPlayer(this.rank, this.nickname, this.score ?: 0, this.playerId)

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


@Component
class MostChallengesDonePolicy(
    private val broadcastEvents: BroadcastEvents,
    private val mostChallengesDoneProjection: MostChallengesDoneLeaderboardProjection //which is really just a stupid normalized database table with a ranking
) {

    private val logger = LoggerFactory.getLogger(MostChallengesDonePolicy::class.java)

    @Scheduled(fixedDelay = 1000 * 60 * 5) // Every 5 minutes
    fun regenerateLeaderboard() {
        val events: List<BroadcastEvent> = broadcastEvents.findAll()

        MostChallengesDoneLeaderboard.rehydrate(events)
            .project()
            .regenerate()
    }

    private fun List<ProjectedPlayer>.regenerate() {
        logger.info("Regenerate triggered with $size players")
        mostChallengesDoneProjection.wipe()
        mostChallengesDoneProjection.store(this)
    }
}

@Component
class RehydrateLeaderboardsHandler(
    private val mostChallengesDonePolicy: MostChallengesDonePolicy
) : CommandHandler<Unit, RehydrateLeaderboards> {

    override fun handle(cmd: RehydrateLeaderboards): Pair<Unit, DomainEvent> {
        mostChallengesDonePolicy.regenerateLeaderboard()
        return Unit to MostChallengesDoneLeaderboardRehydrated
    }

    override val commandType
        get() = RehydrateLeaderboards::class
}

object MostChallengesDoneLeaderboardRehydrated: DomainEvent()