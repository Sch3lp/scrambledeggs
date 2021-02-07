package org.scrambled.domain.core.api.players

import org.scrambled.domain.core.api.challenging.PlayerId

interface IPlayers {
    fun getById(id: PlayerId): PlayerFromDb?
    fun persist(player: PlayerFromDb)
}

data class PlayerFromDb(
    val id: PlayerId,
    val nickname: String
)