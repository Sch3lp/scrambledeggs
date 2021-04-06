package org.scrambled.domain.leaderboards.api.mostchallengesdone.projections

import org.scrambled.infra.cqrs.Command

typealias Rank = Int
typealias Nickname = String
typealias Score = Int

data class ProjectedPlayer(
    val rank: Rank? = null,
    val nickname: Nickname,
    val score: Score
)

interface MostChallengesDoneLeaderboardProjection {
    fun store(players: List<ProjectedPlayer>)
    fun getRanking(): List<ProjectedPlayer>
    fun wipe()
}

object RehydrateLeaderboards: Command<Unit>