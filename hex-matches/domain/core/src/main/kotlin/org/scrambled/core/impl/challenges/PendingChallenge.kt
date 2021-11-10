package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.*
import org.scrambled.domain.api.error.NotFoundException
import org.scrambled.domain.api.error.NotValidException
import org.scrambled.infra.cqrs.QueryHandler
import org.springframework.stereotype.Component
import java.util.*
import kotlin.reflect.KClass

data class AcceptedChallenge(val id: UUID)

data class PendingChallenge(
    val id: UUID,
    val challengeId: ChallengeId,
    val challengerId: ChallengerId,
    val opponentId: OpponentId,
    val comment: UsefulString,
    val appointmentSuggestion: UsefulString,
    val gameMode: GameMode
) {
    init {
        validateYouCannotChallengeYourself(challengerId, opponentId)
    }

    private fun validateYouCannotChallengeYourself(
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
        ): PendingChallenge {
            return PendingChallenge(
                UUID.randomUUID(),
                ChallengeId.newChallengeId(),
                challengerId,
                opponentId,
                comment,
                appointmentSuggestion,
                gameMode
            )
        }
    }
}

@Component
class PendingChallengesForHandler(
    private val pendingChallengesRepo: QueryableChallenges
) : QueryHandler<PendingChallengesFor, List<QueryablePendingChallenge>> {
    override val queryType: KClass<PendingChallengesFor> = PendingChallengesFor::class

    override fun handle(query: PendingChallengesFor): List<QueryablePendingChallenge> =
        pendingChallengesRepo.findPendingFor(query.challengedPlayerId).swapIfNecessary(query.challengedPlayerId)
}

@Component
class PendingChallengeByIdHandler(
    private val pendingChallengesRepo: QueryableChallenges
) : QueryHandler<PendingChallengeById, QueryablePendingChallenge> {
    override val queryType: KClass<PendingChallengeById> = PendingChallengeById::class

    override fun handle(query: PendingChallengeById): QueryablePendingChallenge =
        pendingChallengesRepo.getByChallengeId(query.challengeId)
            ?: throw NotFoundException("Could not find Pending Challenge with id ${query.challengeId}")

}

internal fun List<QueryablePendingChallenge>.swapIfNecessary(challengedPlayerId: PlayerId) =
    map { it.swapIfNecessary(challengedPlayerId) }

internal fun QueryablePendingChallenge.swapIfNecessary(challengedPlayerId: PlayerId) =
    if (this.challengerId != challengedPlayerId) {
        this.swapPlayers()
    } else {
        this
    }

internal fun QueryablePendingChallenge.swapPlayers(): QueryablePendingChallenge =
    this.copy(
        challengerId = this.opponentId, challengerName = this.opponentName,
        opponentId = this.challengerId, opponentName = this.opponentName,
    )
