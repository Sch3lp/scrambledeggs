package org.scrambled.domain.leaderboards.api.mostchallengesdone.projections

typealias Rank = Int
typealias Nickname = String
typealias Score = Int

data class ProjectedPlayer(
    val rank: Rank? = null,
    val nickname: Nickname,
    val score: Score
)
data class MostChallengesDoneLeaderboardProjection(
    val players: List<ProjectedPlayer>
)

interface MostChallengesDoneLeaderboardRepository {
    fun save(projection: MostChallengesDoneLeaderboardProjection)
}