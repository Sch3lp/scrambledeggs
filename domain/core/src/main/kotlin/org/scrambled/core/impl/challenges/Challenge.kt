package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.challenging.ChallengeId
import org.scrambled.domain.core.api.challenging.ChallengerId
import org.scrambled.domain.core.api.challenging.OpponentId
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.exceptions.NotValidException
import java.util.*

data class Challenge(val id: ChallengeId,
                     val challengerId: ChallengerId,
                     val opponentId: OpponentId) {
    init {
        validateNotChallengingYourself(challengerId, opponentId)
    }

    private fun validateNotChallengingYourself(
        challengerId: PlayerId,
        opponentId: PlayerId
    ) {
        if (challengerId == opponentId) {
            throw NotValidException("You cannot challenge yourself.")
        }
    }

    companion object {
        fun createChallenge(challengerId: PlayerId, opponentId: PlayerId): Challenge {
            return Challenge(UUID.randomUUID(), challengerId, opponentId)
        }
    }
}