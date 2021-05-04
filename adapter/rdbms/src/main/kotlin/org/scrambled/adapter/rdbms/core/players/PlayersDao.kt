package org.scrambled.adapter.rdbms.core.players

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.QueryablePlayers
import org.scrambled.domain.core.api.players.QueryablePlayer
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub


@RegisterKotlinMapper(value = QueryablePlayer::class)
interface PlayersDao : QueryablePlayers {
    @SqlQuery("SELECT id, nickname, jwtiss, jwtsub FROM REGISTERED_PLAYERS where id = :id")
    override fun getById(@Bind("id") id: PlayerId): QueryablePlayer?

    @SqlUpdate("INSERT INTO REGISTERED_PLAYERS(id, nickname, jwtiss, jwtsub) values(:id, :nickname, :jwtIss, :jwtSub)")
    override fun store(@BindKotlin player: QueryablePlayer)

    @SqlQuery("SELECT id, nickname, jwtiss, jwtsub FROM REGISTERED_PLAYERS")
    override fun all(): List<QueryablePlayer>

    @SqlQuery("SELECT EXISTS(SELECT 1 FROM REGISTERED_PLAYERS where jwtiss = :jwtIss and jwtsub = :jwtSub)")
    override fun existsByExternalAccountRef(jwtIss: JwtIss, jwtSub: JwtSub): Boolean

    @SqlQuery("SELECT id, nickname, jwtiss, jwtsub FROM REGISTERED_PLAYERS where jwtiss = :jwtIss and jwtsub = :jwtSub")
    override fun findByExternalAccountRef(jwtIss: JwtIss, jwtSub: JwtSub): QueryablePlayer?
}