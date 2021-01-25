package org.scrambled.domain.core.api.players

import org.scrambled.core.impl.players.RegisteredPlayer
import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Query
import kotlin.reflect.KClass

class QueryPlayerById(override val id: AggregateId) : Query<RegisteredPlayer> {
    override val aggregate: KClass<RegisteredPlayer>
        get() = RegisteredPlayer::class
}