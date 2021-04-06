package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerChallenged
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.challenging.PlayerNickname
import org.scrambled.domain.core.api.exceptions.DomainRuntimeException
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.cqrs.QueryHandler
import org.springframework.stereotype.Component
import java.util.*

data class RegisteredPlayer(
    val id: PlayerId,
    val nickName: PlayerNickname,
    val externalAccountRef: ExternalAccountRef
) {
    fun challenge(otherPlayerId: PlayerId): PlayerChallenged {
        return PlayerChallenged(this.id, otherPlayerId)
    }
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
    private val playerRepository: RegisteredPlayerRepository
) : CommandHandler<Unit, ChallengePlayer> {
    override val commandType = ChallengePlayer::class

    override fun handle(cmd: ChallengePlayer): Pair<Unit, PlayerChallenged> {
        val registeredPlayer = playerRepository.getById(cmd.id)
        return Unit to registeredPlayer.execute(cmd)
    }

    private fun RegisteredPlayer.execute(challengePlayer: ChallengePlayer) =
        this.challenge(challengePlayer.otherPlayerId)
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