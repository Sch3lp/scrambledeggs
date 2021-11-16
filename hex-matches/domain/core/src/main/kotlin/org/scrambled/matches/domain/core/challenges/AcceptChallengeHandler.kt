package org.scrambled.matches.domain.core.challenges

import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.matches.domain.api.challenges.AcceptChallenge
import org.scrambled.matches.domain.api.challenges.ChallengeAccepted
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.scrambled.matches.domain.core.players.RegisteredPlayerRepository
import org.springframework.stereotype.Component

@Component
class AcceptChallengeHandler(
    private val challengeRepository: ChallengeRepository,
    private val playerRepository: RegisteredPlayerRepository,
) : CommandHandler<ChallengeId, AcceptChallenge> {
    override val commandType = AcceptChallenge::class

    override fun handle(cmd: AcceptChallenge): Pair<ChallengeId, ChallengeAccepted> {
        val player = playerRepository.getById(cmd.playerId)
        val pendingChallenge = challengeRepository.getPendingByChallengeId(cmd.challengeId)
        player.accept(pendingChallenge).save()
        return pendingChallenge.challengeId to ChallengeAccepted(pendingChallenge.challengeId)
    }

    private fun AcceptedChallenge.save() = challengeRepository.save(this)
}