package org.scrambled.domain.core.api.players

import org.scrambled.domain.core.api.challenging.PlayerId

interface QueryablePlayers {
    fun getById(id: PlayerId): QueryablePlayer?
    fun store(player: QueryablePlayer)
}

data class QueryablePlayer(
    val id: PlayerId,
    val nickname: String
)