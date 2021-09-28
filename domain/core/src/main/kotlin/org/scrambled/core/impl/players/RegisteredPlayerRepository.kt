package org.scrambled.core.impl.players

import org.scrambled.domain.core.api.Repository
import org.scrambled.domain.core.api.challenges.PlayerId
import org.scrambled.domain.core.api.exceptions.NotFoundException
import org.scrambled.domain.core.api.players.QueryablePlayer
import org.scrambled.domain.core.api.players.QueryablePlayers
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.springframework.stereotype.Component

@Component
class RegisteredPlayerRepository(
    val players: QueryablePlayers
) : Repository<RegisteredPlayer> {

    override fun getById(id: PlayerId) =
        players.getById(id)?.toRegisteredPlayer()
            ?: throw NotFoundException("Couldn't find Player with id $id")

    fun getByExternalAccountRef(externalAccountRef: ExternalAccountRef) =
        players.findByExternalAccountRef(externalAccountRef.jwtIss, externalAccountRef.jwtSub)?.toRegisteredPlayer()

    override fun save(aggregate: RegisteredPlayer) =
        QueryablePlayer(
            aggregate.id,
            aggregate.nickName,
            aggregate.externalAccountRef.jwtIss,
            aggregate.externalAccountRef.jwtSub,
        ).save()

    fun getAll() = players.all().toRegisteredPlayers()

    fun existsByExternalAccountRef(externalAccountRef: ExternalAccountRef): Boolean {
        return players.existsByExternalAccountRef(externalAccountRef.jwtIss, externalAccountRef.jwtSub)
    }

    private fun QueryablePlayer.save() = players.store(this)
    private fun QueryablePlayer.toRegisteredPlayer() = RegisteredPlayer(this.id, this.nickname, ExternalAccountRef(this.jwtIss, this.jwtSub))
    private fun List<QueryablePlayer>.toRegisteredPlayers() = this.map { it.toRegisteredPlayer() }
}