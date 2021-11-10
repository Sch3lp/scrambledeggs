package org.scrambled.domain.core.api.players

import org.scrambled.domain.api.security.ExternalAccountRef
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.challenges.PlayerNickname
import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Query

class PlayerById(val id: AggregateId) : Query<RegisteredPlayerRepresentation>
class PlayerByExternalAccountRef(val externalAccountRef: ExternalAccountRef) : Query<RegisteredPlayerRepresentation>

object FetchAllRegisteredPlayers : Query<List<RegisteredPlayerRepresentation>>

data class RegisteredPlayerRepresentation(val id: PlayerId, val nickname: PlayerNickname)