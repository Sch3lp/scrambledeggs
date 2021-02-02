package org.scrambled.core.impl.players

import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Repository
import org.springframework.stereotype.Component

@Component
class RegisteredPlayerRepository: Repository<RegisteredPlayer> {
    override fun getById(id: AggregateId): RegisteredPlayer? {
        return null
    }

    override fun save(registeredPlayer: RegisteredPlayer) {

    }
}