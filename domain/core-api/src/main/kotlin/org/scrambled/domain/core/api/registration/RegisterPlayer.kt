package org.scrambled.domain.core.api.registration

import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.cqrs.DomainEvent
import java.util.*

data class RegisterPlayer(val nickname: String): Command<RegisteredPlayerRepresentation>

data class PlayerRegistered(
    val id: PlayerId,
    val nickName: String
) : DomainEvent()

