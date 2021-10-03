package org.scrambled.domain.core.api.challenges

import java.util.*

interface QueryablePendingChallenges {
    fun findPendingFor(challengedPlayerId: PlayerId): List<QueryablePendingChallenge>
}

data class QueryablePendingChallenge(
    val id: UUID,
    val challengeId: String,
    val gameMode: GameMode,
    val opponentName: String,
    val appointment: String,
)