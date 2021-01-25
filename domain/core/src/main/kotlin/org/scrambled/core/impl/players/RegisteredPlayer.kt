package org.scrambled.core.impl.players

import java.util.*

typealias PlayerId = UUID
typealias PlayerNickname = String

data class RegisteredPlayer(
    val id: PlayerId,
    val nickName: PlayerNickname
) {
    private lateinit var challengedPlayers: List<PlayerId>

    fun challenge(otherPlayerId: PlayerId) {
        this.challengedPlayers += otherPlayerId
    }
}