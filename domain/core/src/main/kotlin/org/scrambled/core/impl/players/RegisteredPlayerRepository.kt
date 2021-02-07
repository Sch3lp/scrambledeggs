package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.Repository
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.scrambled.domain.core.api.players.IPlayers
import org.scrambled.domain.core.api.players.PlayerFromDb
import org.springframework.stereotype.Component

@Component
class RegisteredPlayerRepository(
    val playerDao: IPlayers
) : Repository<RegisteredPlayer> {

    override fun getById(id: PlayerId) =
        playerDao.getById(id)?.toRegisteredPlayer()
            ?: throw NotFoundException("Couldn't find Player with id $id")

    override fun save(aggregate: RegisteredPlayer) {
        val toSave = PlayerFromDb(aggregate.id, aggregate.nickName)
        playerDao.persist(toSave)
    }
}

fun PlayerFromDb.toRegisteredPlayer() = RegisteredPlayer(this.id, this.nickname)