package org.scrambled.domain.core.api.players

import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub

interface QueryablePlayers {
    fun getById(id: PlayerId): QueryablePlayer?
    fun store(player: QueryablePlayer)
    fun all(): List<QueryablePlayer>
}

data class QueryablePlayer(
    val id: PlayerId,
    val nickname: String,
    val jwtIss: JwtIss,
    val jwtSub: JwtSub
)