package org.scrambled.domain.core.api.challenging

import org.scrambled.core.impl.players.PlayerId
import org.scrambled.core.impl.players.RegisteredPlayer
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.cqrs.DomainEvent
import kotlin.reflect.KClass

data class ChallengePlayer(
    override val id: PlayerId, val otherPlayerId: PlayerId
): Command<RegisteredPlayer> {
    override val aggregate: KClass<RegisteredPlayer>
        get() = RegisteredPlayer::class

    override fun RegisteredPlayer.execute(): PlayerChallenged {
        this.challenge(otherPlayerId)
        return PlayerChallenged(this.id, otherPlayerId)
    }

}

data class PlayerChallenged(val initiator: PlayerId, val opponent: PlayerId): DomainEvent()