package org.scrambled.matches.adapters.out.rdbms.players

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.common.domain.api.security.JwtIss
import org.scrambled.common.domain.api.security.JwtSub
import org.scrambled.matches.domain.api.challenges.PlayerId
import org.scrambled.matches.domain.api.players.QueryablePlayer
import org.scrambled.matches.domain.api.players.QueryablePlayers


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