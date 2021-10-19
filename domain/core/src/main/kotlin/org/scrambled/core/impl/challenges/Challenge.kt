package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.*
import org.scrambled.domain.core.api.exceptions.NotValidException
import org.scrambled.infra.cqrs.QueryHandler
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.KClass

data class AcceptedChallenge(val id: UUID)

data class Challenge(
    val id: UUID,
    val challengeId: ChallengeId,
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: UsefulString,
    val appointmentSuggestion: UsefulString,
    val gameMode: GameMode
) {
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

    fun accept() = AcceptedChallenge(this.id)

    companion object {
        fun createChallenge(
            challengerId: PlayerId,
            opponentId: PlayerId,
            comment: UsefulString,
            appointmentSuggestion: UsefulString,
            gameMode: GameMode,
        ): Challenge {
            return Challenge(UUID.randomUUID(), ChallengeId.newChallengeId(), challengerId, opponentId, comment, appointmentSuggestion, gameMode)
        }
    }
}

@Component
class PendingChallengesForHandler(
    private val pendingChallengesRepo: QueryablePendingChallenges
) : QueryHandler<PendingChallengesFor, List<QueryablePendingChallenge>> {
    override val queryType: KClass<PendingChallengesFor> = PendingChallengesFor::class

    override fun handle(query: PendingChallengesFor): List<QueryablePendingChallenge> =
        pendingChallengesRepo
            .findPendingFor(query.challengedPlayerId)
            .map { challenge ->
                QueryablePendingChallenge(
                    challenge.id,
                    challenge.challengeId,
                    challenge.gameMode,
                    challenge.opponentName,
                    challenge.appointment,
                )
            }

}
