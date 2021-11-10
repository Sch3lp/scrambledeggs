package org.scrambled.matches.domain.core.challenges

import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.scrambled.matches.domain.api.challenges.QueryableChallenge
import org.scrambled.matches.domain.api.challenges.QueryableChallenges
import org.scrambled.matches.domain.api.challenges.QueryablePendingChallenge
import org.scrambled.common.domain.api.error.NotFoundException
import org.springframework.stereotype.Component

@Component
class ChallengeRepository(
    private val challenges: QueryableChallenges
) {
    fun getPendingByChallengeId(challengeId: ChallengeId): PendingChallenge =
        challenges.getByChallengeId(challengeId)?.toPendingChallenge()
            ?: throw NotFoundException("Couldn't find Pending Challenge with challengeId $challengeId")

    fun exists(challengeId: ChallengeId) =
        challenges.getByChallengeId(challengeId.id)?.let { true } ?: false

    fun save(aggregate: PendingChallenge) =
        QueryableChallenge(
            aggregate.id,
            aggregate.challengeId.id,
            aggregate.challengerId,
            aggregate.opponentId,
            aggregate.comment.value,
            aggregate.appointmentSuggestion.value,
            aggregate.gameMode,
            false
        ).save()

    fun save(aggregate: AcceptedChallenge) =
        challenges.storeAcceptedChallenge(aggregate.id)


    private fun QueryableChallenge.save() = challenges.storePendingChallenge(this)

    private fun QueryablePendingChallenge.toPendingChallenge() = if (this.isAccepted) null else {
        PendingChallenge(
            this.id,
            ChallengeId.challengeId(this.challengeId),
            this.challengerId,
            this.opponentId,
            UsefulString(this.comment),
            UsefulString(this.appointment),
            this.gameMode,
        )
    }
}