package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerChallenged
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.challenging.PlayerNickname
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.cqrs.QueryHandler
import org.springframework.stereotype.Component
import java.util.*


data class RegisteredPlayer(
    val id: PlayerId,
    val nickName: PlayerNickname
) {
    private lateinit var challengedPlayers: List<PlayerId>

    fun challenge(otherPlayerId: PlayerId): PlayerChallenged {
        this.challengedPlayers += otherPlayerId
        return PlayerChallenged(this.id, otherPlayerId)
    }
}

@Component
class RegisterPlayerHandler(
    private val playerRepository: RegisteredPlayerRepository
) : CommandHandler<RegisteredPlayerRepresentation, RegisterPlayer> {
    override val commandType = RegisterPlayer::class

    override fun handle(cmd: RegisterPlayer): Pair<RegisteredPlayerRepresentation, PlayerRegistered> {
        val registeredPlayer = RegisteredPlayer(generatePlayerId(), cmd.nickname)

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

fun RegisteredPlayer.toRepresentation(): RegisteredPlayerRepresentation =
    RegisteredPlayerRepresentation(this.id, this.nickName)