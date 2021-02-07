package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.Repository
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.scrambled.domain.core.api.players.QueryablePlayers
import org.scrambled.domain.core.api.players.QueryablePlayer
import org.springframework.stereotype.Component

@Component
class RegisteredPlayerRepository(
    val players: QueryablePlayers
) : Repository<RegisteredPlayer> {

    override fun getById(id: PlayerId) =
        players.getById(id)?.toRegisteredPlayer()
            ?: throw NotFoundException("Couldn't find Player with id $id")

    override fun save(aggregate: RegisteredPlayer) =
        QueryablePlayer(aggregate.id, aggregate.nickName).save()

    fun QueryablePlayer.save() = players.store(this)
    fun QueryablePlayer.toRegisteredPlayer() = RegisteredPlayer(this.id, this.nickname)
}