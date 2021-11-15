package org.scrambled.matches.domain.core.challenges

import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.retry.retry
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.scrambled.matches.domain.api.challenges.ChallengePlayer
import org.scrambled.matches.domain.api.challenges.PlayerChallenged
import org.scrambled.matches.domain.core.players.RegisteredPlayerRepository
import org.springframework.stereotype.Component

@Component
class ChallengePlayerHandler(
    private val playerRepository: RegisteredPlayerRepository,
    private val challengeRepository: ChallengeRepository
) : CommandHandler<ChallengeId, ChallengePlayer> {
    override val commandType = ChallengePlayer::class

    override fun handle(cmd: ChallengePlayer): Pair<ChallengeId, PlayerChallenged> {
        val challenger = playerRepository.getById(cmd.challenger)
        val opponent = playerRepository.getById(cmd.opponent)
        val pendingChallenge: PendingChallenge =
            retry("Couldn't create a pending challenge with unique id.") {
                challenger.challenge(opponent, cmd.comment, cmd.appointmentSuggestion, cmd.gameMode)
            }.until { challenge -> !challengeRepository.exists(challenge.challengeId) }
        challengeRepository.save(pendingChallenge)
        return pendingChallenge.challengeId to PlayerChallenged(challenger.id, opponent.id)
    }
}