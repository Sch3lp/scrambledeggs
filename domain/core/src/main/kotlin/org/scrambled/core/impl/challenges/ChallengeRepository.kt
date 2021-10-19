package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.Repository
import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.ChallengeId
import org.scrambled.domain.core.api.challenges.QueryableChallenge
import org.scrambled.domain.core.api.challenges.QueryableChallenges
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.scrambled.infra.cqrs.AggregateId
import org.springframework.stereotype.Component

@Component
class ChallengeRepository(
    private val challenges: QueryableChallenges
) : Repository<Challenge> {

    override fun getById(id: AggregateId): Challenge =
        challenges.getById(id)?.toChallenge()
            ?: throw NotFoundException("Couldn't find Challenge with id $id")

    override fun save(aggregate: Challenge) =
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

    fun getByChallengeId(challengeId: ChallengeId) =
        challenges.getByChallengeId(challengeId.id)?.toChallenge()
            ?: throw NotFoundException("Couldn't find Challenge with challengeId $challengeId")

    fun exists(challengeId: ChallengeId) =
        challenges.getByChallengeId(challengeId.id)?.let { true } ?: false



    private fun QueryableChallenge.save() = challenges.store(this)

    private fun QueryableChallenge.toChallenge() = Challenge(
        this.id,
        ChallengeId.challengeId(this.challengeId),
        this.challengerId,
        this.opponentId,
        UsefulString(this.comment),
        UsefulString(this.appointmentSuggestion),
        this.gameMode,
    )
}