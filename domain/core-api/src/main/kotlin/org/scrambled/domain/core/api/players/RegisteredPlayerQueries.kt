package org.scrambled.domain.core.api.players

import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.challenges.PlayerNickname
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Query

class PlayerById(override val id: AggregateId) : Query<RegisteredPlayerRepresentation>
class PlayerByExternalAccountRef(val externalAccountRef: ExternalAccountRef) : Query<RegisteredPlayerRepresentation> {
    override val id: AggregateId = AggregateId.randomUUID()
    //TODO: jezus, refactor this so that Query doesn't REQUIRE an AggregateId.
    //TODO: you know, for the cases where we want to fetch ALL of the stuffs...?
}

object FetchAllRegisteredPlayers : Query<List<RegisteredPlayerRepresentation>> {
    override val id: AggregateId = AggregateId.randomUUID()
    //TODO: jezus, refactor this so that Query doesn't REQUIRE an AggregateId.
    //TODO: you know, for the cases where we want to fetch ALL of the stuffs...?
}

data class RegisteredPlayerRepresentation(val id: PlayerId, val nickname: PlayerNickname)