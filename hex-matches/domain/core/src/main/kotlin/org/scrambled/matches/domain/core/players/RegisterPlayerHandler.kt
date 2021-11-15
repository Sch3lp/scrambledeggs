package org.scrambled.matches.domain.core.players

import org.scrambled.common.domain.api.error.DomainRuntimeException
import org.scrambled.infra.cqrs.CommandHandler
import org.scrambled.matches.domain.api.challenges.PlayerId
import org.scrambled.matches.domain.api.players.RegisteredPlayerRepresentation
import org.scrambled.matches.domain.api.registration.PlayerRegistered
import org.scrambled.matches.domain.api.registration.RegisterPlayer
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegisterPlayerHandler(
    private val playerRepository: RegisteredPlayerRepository
) : CommandHandler<RegisteredPlayerRepresentation, RegisterPlayer> {
    override val commandType = RegisterPlayer::class

    override fun handle(cmd: RegisterPlayer): Pair<RegisteredPlayerRepresentation, PlayerRegistered> {
        if (playerRepository.existsByExternalAccountRef(cmd.externalAccountRef)) {
            throw DomainRuntimeException("You can only register once with the same Epic account")
        }

        val registeredPlayer = RegisteredPlayer(generatePlayerId(), cmd.nickname, cmd.externalAccountRef)

        registeredPlayer.save()

        return registeredPlayerRepresentation(registeredPlayer) to
                PlayerRegistered(registeredPlayer.id, registeredPlayer.nickName)
    }

    private fun RegisteredPlayer.save() = playerRepository.save(this)

    private fun generatePlayerId(): PlayerId = UUID.randomUUID()

    private fun registeredPlayerRepresentation(registeredPlayer: RegisteredPlayer) =
        RegisteredPlayerRepresentation(registeredPlayer.id, registeredPlayer.nickName)
}