package org.scrambled.domain.core.api.registration

import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
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


typealias JwtIss = String
typealias JwtSub = String

data class ExternalAccountRef(
    val jwtIss: JwtIss, val jwtSub: JwtSub
)
