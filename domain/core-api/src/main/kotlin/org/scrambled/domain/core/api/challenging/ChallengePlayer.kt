package org.scrambled.domain.core.api.challenging

import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.cqrs.DomainEvent
import java.util.*

data class ChallengePlayer(
    val id: ChallengerId, val otherPlayerId: OpponentId
): Command

data class PlayerChallenged(val initiator: ChallengerId, val opponent: OpponentId): DomainEvent()

typealias ChallengerId = PlayerId
typealias OpponentId = PlayerId
typealias PlayerId = UUID
typealias PlayerNickname = String
