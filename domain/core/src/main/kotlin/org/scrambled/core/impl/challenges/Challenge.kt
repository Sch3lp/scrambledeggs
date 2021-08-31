package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.challenging.ChallengeId
import org.scrambled.domain.core.api.challenging.ChallengerId
import org.scrambled.domain.core.api.challenging.OpponentId
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.exceptions.NotValidException
import java.util.*

data class Challenge(
    val id: ChallengeId,
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: String,
    val appointmentSuggestion: String
) {
    init {
        validateNotChallengingYourself(challengerId, opponentId)
        validateUsefulString(comment)
        validateUsefulString(appointmentSuggestion)
    }

    private fun validateUsefulString(s: String) {
        if (s.isBlank()) {
            throw NotValidException("You cannot challenge without a useful comment or appointment suggestion.")
        }
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
        fun createChallenge(
            challengerId: PlayerId,
            opponentId: PlayerId,
            comment: String,
            appointmentSuggestion: String
        ): Challenge {
            return Challenge(UUID.randomUUID(), challengerId, opponentId, comment, appointmentSuggestion)
        }
    }
}