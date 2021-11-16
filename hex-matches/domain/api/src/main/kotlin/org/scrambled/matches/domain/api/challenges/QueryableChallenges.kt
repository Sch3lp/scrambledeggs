package org.scrambled.matches.domain.api.challenges

import java.util.*

interface QueryableChallenges {
    fun getByChallengeId(challengeId: String): QueryableChallenge?
    fun storePendingChallenge(queryableChallenge: QueryableChallenge)
    fun storeAcceptedChallenge(id: UUID)
    fun getByChallengeId(challengeId: ChallengeId): QueryablePendingChallenge?
    fun findPendingFor(playerId: PlayerId): List<QueryablePendingChallenge>
}

data class QueryableChallenge(
    val id: UUID,
    val challengeId: String, //TODO use ChallengeId value class
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: String,
    val appointmentSuggestion: String,
    val gameMode: GameMode,
    val isAccepted: Boolean
)

data class QueryablePendingChallenge(
    val id: UUID,
    val challengeId: String, //TODO use ChallengeId value class
    val gameMode: GameMode,
    val challengerId: ChallengerId,
    val challengerName: String,
    val opponentId: OpponentId,
    val opponentName: String,
    val appointment: String,
    val comment: String,
    val isAccepted: Boolean,
)