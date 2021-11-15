package org.scrambled.matches.domain.core.players

import org.scrambled.matches.domain.core.challenges.PendingChallenge
import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.*
import org.scrambled.common.domain.api.security.ExternalAccountRef
import org.scrambled.matches.domain.api.players.FetchAllRegisteredPlayers
import org.scrambled.matches.domain.api.players.PlayerByExternalAccountRef
import org.scrambled.matches.domain.api.players.PlayerById
import org.scrambled.matches.domain.api.players.RegisteredPlayerRepresentation
import org.scrambled.infra.cqrs.QueryHandler
import org.springframework.stereotype.Component

data class RegisteredPlayer(
    val id: PlayerId,
    val nickName: PlayerNickname,
    val externalAccountRef: ExternalAccountRef
) {
    fun challenge(
        opponent: RegisteredPlayer,
        comment: UsefulString,
        appointmentSuggestion: UsefulString,
        gameMode: GameMode
    ) =
        PendingChallenge.createChallenge(
            challengerId = this.id,
            opponentId = opponent.id,
            comment = comment,
            appointmentSuggestion = appointmentSuggestion,
            gameMode = gameMode
        )
}


@Component
class PlayerByIdQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<PlayerById, RegisteredPlayerRepresentation> {
    override val queryType = PlayerById::class

    override fun handle(query: PlayerById): RegisteredPlayerRepresentation {
        val registeredPlayer = playerRepository.getById(query.id)
        return registeredPlayer.toRepresentation()
    }
}

@Component
class PlayerByExternalAccountRefQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<PlayerByExternalAccountRef, RegisteredPlayerRepresentation> {
    override val queryType = PlayerByExternalAccountRef::class

    override fun handle(query: PlayerByExternalAccountRef): RegisteredPlayerRepresentation? {
        val registeredPlayer = playerRepository.getByExternalAccountRef(query.externalAccountRef)
        return registeredPlayer?.toRepresentation()
    }
}

@Component
class FetchAllRegisteredPlayersQueryHandler(
    private val playerRepository: RegisteredPlayerRepository
) : QueryHandler<FetchAllRegisteredPlayers, List<RegisteredPlayerRepresentation>> {
    override val queryType = FetchAllRegisteredPlayers::class

    override fun handle(query: FetchAllRegisteredPlayers): List<RegisteredPlayerRepresentation> {
        val registeredPlayers: List<RegisteredPlayer> = playerRepository.getAll()
        return registeredPlayers.toRepresentation()
    }
}

internal fun RegisteredPlayer.toRepresentation(): RegisteredPlayerRepresentation =
    RegisteredPlayerRepresentation(this.id, this.nickName)

internal fun List<RegisteredPlayer>.toRepresentation() = this.map(RegisteredPlayer::toRepresentation)