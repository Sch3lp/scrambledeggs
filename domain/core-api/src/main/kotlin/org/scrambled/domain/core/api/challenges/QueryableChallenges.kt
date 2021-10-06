package org.scrambled.domain.core.api.challenges

import java.util.*

interface QueryableChallenges {
    fun getById(id: UUID): QueryableChallenge?
    fun getByChallengeId(challengeId: String): QueryableChallenge?
    fun store(queryableChallenge: QueryableChallenge)
}

data class QueryableChallenge(
    val id: UUID,
    val challengeId: String,
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: String,
    val appointmentSuggestion: String,
    val gameMode: GameMode = GameMode.CTF,
    val isAccepted: Boolean
)