package org.scrambled.domain.core.api.challenges

interface QueryablePendingChallenges {
    fun findPendingFor(challengedPlayerId: PlayerId): List<QueryablePendingChallenge>
}

data class QueryablePendingChallenge(
    val challengeId: ChallengeId,
    val gameMode: GameMode,
    val opponentName: String,
    val appointment: String,
)