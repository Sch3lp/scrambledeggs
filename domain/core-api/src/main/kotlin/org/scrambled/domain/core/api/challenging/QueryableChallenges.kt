package org.scrambled.domain.core.api.challenging

interface QueryableChallenges {
    fun getById(id: ChallengeId): QueryableChallenge?
    fun store(queryableChallenge: QueryableChallenge)
}

data class QueryableChallenge(val id: ChallengeId,
                              val challengerId: ChallengerId,
                              val opponentId: OpponentId)