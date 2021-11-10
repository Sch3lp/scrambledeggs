package org.scrambled.matches.domain.api.registration

import org.scrambled.common.domain.api.security.ExternalAccountRef
import org.scrambled.matches.domain.api.challenges.PlayerId
import org.scrambled.matches.domain.api.players.RegisteredPlayerRepresentation
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.domainevents.DomainEvent

data class RegisterPlayer(
    val nickname: String,
    val externalAccountRef: ExternalAccountRef
) : Command<RegisteredPlayerRepresentation>

data class PlayerRegistered(
    val playerId: PlayerId,
    val nickName: String
) : DomainEvent()

