package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerChallenged
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.challenging.PlayerNickname
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.*
import org.springframework.stereotype.Component


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
) : CommandHandler<RegisterPlayer> {
    override fun handle(cmd: RegisterPlayer): DomainEvent {
        val registeredPlayer = RegisteredPlayer(cmd.id, cmd.nickName)
        playerRepository.save(registeredPlayer)
        return PlayerRegistered(registeredPlayer.id, registeredPlayer.nickName)
    }

    override fun canHandle(commandType: Class<out RegisterPlayer>): Boolean {
        return commandType == RegisterPlayer::class.java
    }
}


@Component
class ChallengePlayerHandler(
    private val playerRepository: RegisteredPlayerRepository
): CommandHandler<ChallengePlayer> {
    override fun handle(cmd: ChallengePlayer): PlayerChallenged {
        val registeredPlayer = playerRepository.getById(cmd.id)
            ?: throw RuntimeException("No Aggregate found for id ${cmd.id}")
        return registeredPlayer.execute(cmd)
    }

    override fun canHandle(commandType: Class<out ChallengePlayer>): Boolean {
        return commandType == ChallengePlayer::class.java
    }
}
fun RegisteredPlayer.execute(challengePlayer: ChallengePlayer) =
    this.challenge(challengePlayer.otherPlayerId)


class PlayerByIdQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
): QueryHandler<PlayerById, RegisteredPlayerRepresentation> {
    override fun handle(query: PlayerById): RegisteredPlayerRepresentation {
        val registeredPlayer = playerRepository.getById(query.id)
            ?: throw RuntimeException("No Player found for ${query.id}")
        return registeredPlayer.toRepresentation()
    }
}
fun RegisteredPlayer.toRepresentation(): RegisteredPlayerRepresentation = RegisteredPlayerRepresentation(this.id, this.nickName)