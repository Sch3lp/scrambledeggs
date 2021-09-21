package org.scrambled.core.impl.challenges

import org.scrambled.domain.core.api.Repository
import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenging.ChallengeId
import org.scrambled.domain.core.api.challenging.QueryableChallenge
import org.scrambled.domain.core.api.challenging.QueryableChallenges
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.springframework.stereotype.Component

@Component
class ChallengeRepository(
    val challenges: QueryableChallenges
) : Repository<Challenge> {

    override fun getById(id: ChallengeId) =
        challenges.getById(id)?.toChallenge()
            ?: throw NotFoundException("Couldn't find Challenge with id $id")

    override fun save(aggregate: Challenge) =
        QueryableChallenge(
            aggregate.id,
            aggregate.challengerId,
            aggregate.opponentId,
            aggregate.comment.value,
            aggregate.appointmentSuggestion.value,
            aggregate.gameMode
        ).save()

    private fun QueryableChallenge.save() = challenges.store(this)
    private fun QueryableChallenge.toChallenge() = Challenge(
        this.id,
        this.challengerId,
        this.opponentId,
        UsefulString(this.comment),
        UsefulString(this.appointmentSuggestion),
        this.gameMode,
    )
}