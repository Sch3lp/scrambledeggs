package org.scrambled.domain.core.api.challenging

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.domainevents.DomainEvent
import java.util.*

data class ChallengePlayer(
    val challenger: ChallengerId,
    val opponent: OpponentId,
    val comment: UsefulString,
    val appointmentSuggestion: UsefulString,
    val gameMode: GameMode
) : Command<ChallengeId>

data class PlayerChallenged(val challenger: ChallengerId, val opponent: OpponentId) : DomainEvent()

typealias ChallengerId = PlayerId
typealias OpponentId = PlayerId
typealias PlayerId = UUID
typealias PlayerNickname = String
typealias ChallengeId = UUID

enum class GameMode {
    Duel, TwoVsTwo, WipeOut, CTF
}