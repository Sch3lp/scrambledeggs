package org.scrambled.domain.leaderboards.api.mostchallengesdone.projections

import org.scrambled.infra.cqrs.Command
import java.util.*

typealias Rank = Int
typealias Nickname = String
typealias Score = Int
typealias PlayerId = UUID

data class ProjectedPlayer(
    val rank: Rank? = null,
    val nickname: Nickname,
    val score: Score,
    val playerId: PlayerId,
)

interface MostChallengesDoneLeaderboardProjection {
    fun store(players: List<ProjectedPlayer>)
    fun getRanking(): List<ProjectedPlayer>
    fun wipe()
}

object RehydrateLeaderboards: Command<Unit>