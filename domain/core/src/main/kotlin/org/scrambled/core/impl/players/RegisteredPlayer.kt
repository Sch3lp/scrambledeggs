package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.challenging.ChallengePlayer
import org.scrambled.domain.core.api.challenging.PlayerChallenged
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.challenging.PlayerNickname
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.infra.cqrs.repositoryForAggregate


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

class ChallengePlayerHandler: CommandHandler<ChallengePlayer> {
    override fun handle(cmd: ChallengePlayer): PlayerChallenged {
        val registeredPlayer = repositoryForAggregate<RegisteredPlayer>().getById(cmd.id)
            ?: throw RuntimeException("No Aggregate found for id ${cmd.id}")
        return registeredPlayer.execute(cmd)
    }
}

fun RegisteredPlayer.execute(challengePlayer: ChallengePlayer) =
    this.challenge(challengePlayer.otherPlayerId)
