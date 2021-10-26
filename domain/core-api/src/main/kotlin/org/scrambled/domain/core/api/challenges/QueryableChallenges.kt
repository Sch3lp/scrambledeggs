package org.scrambled.domain.core.api.challenges

import java.util.*

interface QueryableChallenges {
    fun getByChallengeId(challengeId: String): QueryableChallenge?
    fun getPendingByChallengeId(challengeId: String): QueryableChallenge?
    fun storePendingChallenge(queryableChallenge: QueryableChallenge)
    fun storeAcceptedChallenge(id: UUID)
}

data class QueryableChallenge(
    val id: UUID,
    val challengeId: String,
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: String,
    val appointmentSuggestion: String,
    val gameMode: GameMode,
    val isAccepted: Boolean
)