package org.scrambled.matches.domain.core.challenges

import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.matches.domain.api.challenges.AcceptChallenge
import org.scrambled.matches.domain.api.challenges.ChallengeAccepted
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.springframework.stereotype.Component

@Component
class AcceptChallengeHandler(
    private val challengeRepository: ChallengeRepository
) : CommandHandler<ChallengeId, AcceptChallenge> {
    override val commandType = AcceptChallenge::class

    override fun handle(cmd: AcceptChallenge): Pair<ChallengeId, ChallengeAccepted> {
        val pendingChallenge = challengeRepository.getPendingByChallengeId(cmd.challengeId)
        pendingChallenge.accept().save()
        return pendingChallenge.challengeId to ChallengeAccepted(pendingChallenge.challengeId)
    }

    private fun AcceptedChallenge.save() = challengeRepository.save(this)
}