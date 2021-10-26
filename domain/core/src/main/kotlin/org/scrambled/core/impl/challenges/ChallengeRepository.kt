package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.ChallengeId
import org.scrambled.domain.core.api.challenges.QueryableChallenge
import org.scrambled.domain.core.api.challenges.QueryableChallenges
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.springframework.stereotype.Component

@Component
class ChallengeRepository(
    private val challenges: QueryableChallenges
) {

    fun getPendingByChallengeId(challengeId: ChallengeId): PendingChallenge =
        challenges.getPendingByChallengeId(challengeId.id)?.toPendingChallenge()
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

    private fun QueryableChallenge.toPendingChallenge() = if (this.isAccepted) null else {
        PendingChallenge(
            this.id,
            ChallengeId.challengeId(this.challengeId),
            this.challengerId,
            this.opponentId,
            UsefulString(this.comment),
            UsefulString(this.appointmentSuggestion),
            this.gameMode,
        )
    }
}