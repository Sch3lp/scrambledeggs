package org.scrambled.matches.domain.api.players

import org.scrambled.common.domain.api.security.JwtIss
import org.scrambled.common.domain.api.security.JwtSub
import org.scrambled.matches.domain.api.challenges.PlayerId

interface QueryablePlayers {
    fun getById(id: PlayerId): QueryablePlayer?
    fun findByExternalAccountRef(jwtIss: JwtIss, jwtSub: JwtSub): QueryablePlayer?
    fun store(player: QueryablePlayer)
    fun all(): List<QueryablePlayer>
    fun existsByExternalAccountRef(jwtIss: JwtIss, jwtSub: JwtSub): Boolean
}

data class QueryablePlayer(
    val id: PlayerId,
    val nickname: String,
    val jwtIss: JwtIss,
    val jwtSub: JwtSub
)