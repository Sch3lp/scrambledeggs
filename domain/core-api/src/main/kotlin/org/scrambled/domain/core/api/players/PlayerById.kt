package org.scrambled.domain.core.api.players

import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.challenging.PlayerNickname
import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Query

class PlayerById(override val id: AggregateId) : Query<RegisteredPlayerRepresentation>

data class RegisteredPlayerRepresentation(val id: PlayerId, val nickname: PlayerNickname)