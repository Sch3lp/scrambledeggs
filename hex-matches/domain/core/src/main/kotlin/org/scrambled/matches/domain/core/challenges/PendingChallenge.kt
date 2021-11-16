package org.scrambled.matches.domain.core.challenges

import org.scrambled.common.domain.api.error.NotFoundException
import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.*
import org.scrambled.common.domain.api.error.NotValidException
import org.scrambled.infra.cqrs.QueryHandler
import org.scrambled.matches.domain.api.players.QueryablePlayers
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
        pendingChallengesRepo.findPendingFor(query.playerId).swapIfNecessary(query.playerId)
}

@Component
class PendingChallengeByIdHandler(
    private val pendingChallengesRepo: QueryableChallenges,
) : QueryHandler<PendingChallengeById, QueryablePendingChallenge> {
    override val queryType: KClass<PendingChallengeById> = PendingChallengeById::class

    override fun handle(query: PendingChallengeById): QueryablePendingChallenge {
        return pendingChallengesRepo.getByChallengeId(query.challengeId)
            ?: throw NotFoundException("Could not find Pending Challenge with id ${query.challengeId}")
    }
}

internal fun List<QueryablePendingChallenge>.swapIfNecessary(playerId: PlayerId) =
    map { it.swapIfNecessary(playerId) }

internal fun QueryablePendingChallenge.swapIfNecessary(playerId: PlayerId) =
    if (this.opponentId == playerId) {
        this.swapPlayers()
    } else {
        this
    }

internal fun QueryablePendingChallenge.swapPlayers(): QueryablePendingChallenge =
    this.copy(
        challengerId = this.opponentId, challengerName = this.opponentName,
        opponentId = this.challengerId, opponentName = this.challengerName,
    )
