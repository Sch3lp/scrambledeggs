package org.scrambled.core.impl.players

import org.scrambled.core.impl.challenges.AcceptedChallenge
import org.scrambled.core.impl.challenges.PendingChallenge
import org.scrambled.core.impl.challenges.ChallengeRepository
import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.*
import org.scrambled.domain.core.api.exceptions.DomainRuntimeException
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.PlayerByExternalAccountRef
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.cqrs.QueryHandler
import org.scrambled.infra.retry.retry
import org.springframework.stereotype.Component
import java.util.*

data class RegisteredPlayer(
    val id: PlayerId,
    val nickName: PlayerNickname,
    val externalAccountRef: ExternalAccountRef
) {
    fun challenge(
        opponent: RegisteredPlayer,
        comment: UsefulString,
        appointmentSuggestion: UsefulString,
        gameMode: GameMode
    ) =
        PendingChallenge.createChallenge(
            challengerId = this.id,
            opponentId = opponent.id,
            comment = comment,
            appointmentSuggestion = appointmentSuggestion,
            gameMode = gameMode
        )
}

@Component
class RegisterPlayerHandler(
    private val playerRepository: RegisteredPlayerRepository
) : CommandHandler<RegisteredPlayerRepresentation, RegisterPlayer> {
    override val commandType = RegisterPlayer::class

    override fun handle(cmd: RegisterPlayer): Pair<RegisteredPlayerRepresentation, PlayerRegistered> {
        if (playerRepository.existsByExternalAccountRef(cmd.externalAccountRef)) {
            throw DomainRuntimeException("You can only register once with the same Epic account")
        }

        val registeredPlayer = RegisteredPlayer(generatePlayerId(), cmd.nickname, cmd.externalAccountRef)

        registeredPlayer.save()

        return registeredPlayerRepresentation(registeredPlayer) to
                PlayerRegistered(registeredPlayer.id, registeredPlayer.nickName)
    }

    private fun RegisteredPlayer.save() = playerRepository.save(this)

    private fun generatePlayerId(): PlayerId = UUID.randomUUID()

    private fun registeredPlayerRepresentation(registeredPlayer: RegisteredPlayer) =
        RegisteredPlayerRepresentation(registeredPlayer.id, registeredPlayer.nickName)
}


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

@Component
class PlayerByIdQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<PlayerById, RegisteredPlayerRepresentation> {
    override val queryType = PlayerById::class

    override fun handle(query: PlayerById): RegisteredPlayerRepresentation {
        val registeredPlayer = playerRepository.getById(query.id)
        return registeredPlayer.toRepresentation()
    }
}

@Component
class PlayerByExternalAccountRefQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<PlayerByExternalAccountRef, RegisteredPlayerRepresentation> {
    override val queryType = PlayerByExternalAccountRef::class

    override fun handle(query: PlayerByExternalAccountRef): RegisteredPlayerRepresentation? {
        val registeredPlayer = playerRepository.getByExternalAccountRef(query.externalAccountRef)
        return registeredPlayer?.toRepresentation()
    }
}

@Component
class FetchAllRegisteredPlayersQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<FetchAllRegisteredPlayers, List<RegisteredPlayerRepresentation>> {
    override val queryType = FetchAllRegisteredPlayers::class

    override fun handle(query: FetchAllRegisteredPlayers): List<RegisteredPlayerRepresentation> {
        val registeredPlayers: List<RegisteredPlayer> = playerRepository.getAll()
        return registeredPlayers.toRepresentation()
    }
}

internal fun RegisteredPlayer.toRepresentation(): RegisteredPlayerRepresentation =
    RegisteredPlayerRepresentation(this.id, this.nickName)

internal fun List<RegisteredPlayer>.toRepresentation() = this.map(RegisteredPlayer::toRepresentation)