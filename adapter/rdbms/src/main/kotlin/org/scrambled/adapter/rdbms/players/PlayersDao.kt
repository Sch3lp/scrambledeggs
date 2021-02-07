package org.scrambled.adapter.rdbms.players

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.IPlayers
import org.scrambled.domain.core.api.players.PlayerFromDb


@RegisterKotlinMapper(value = PlayerFromDb::class)
interface PlayersDao : IPlayers {
    @SqlQuery("SELECT id, nickname FROM REGISTERED_PLAYERS where id = :id")
    override fun getById(@Bind("id") id: PlayerId): PlayerFromDb?

    @SqlUpdate("INSERT INTO REGISTERED_PLAYERS(id, nickname) values(:id, :nickname)")
    override fun persist(@BindKotlin player: PlayerFromDb)
}