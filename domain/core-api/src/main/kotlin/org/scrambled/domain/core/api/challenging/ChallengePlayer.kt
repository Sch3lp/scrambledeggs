package org.scrambled.domain.core.api.challenging

import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.cqrs.DomainEvent
import java.util.*

data class ChallengePlayer(
    override val id: PlayerId, val otherPlayerId: PlayerId
): Command

data class PlayerChallenged(val initiator: PlayerId, val opponent: PlayerId): DomainEvent()

typealias PlayerId = UUID
typealias PlayerNickname = String
