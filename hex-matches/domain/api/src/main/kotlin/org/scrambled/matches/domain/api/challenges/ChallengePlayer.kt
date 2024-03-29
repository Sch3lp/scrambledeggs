package org.scrambled.matches.domain.api.challenges

import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.infra.cqrs.Command
import org.scrambled.infra.domainevents.DomainEvent
import org.scrambled.infra.hashids.Hashids
import java.util.*

data class ChallengePlayer(
    val challenger: ChallengerId,
    val opponent: OpponentId,
    val comment: UsefulString,
    val appointmentSuggestion: UsefulString,
    val gameMode: GameMode
) : Command<ChallengeId>

data class PlayerChallenged(val challenger: ChallengerId, val opponent: OpponentId) : DomainEvent()

data class AcceptChallenge(val playerId: PlayerId, val challengeId: ChallengeId) : Command<ChallengeId>
data class ChallengeAccepted(val challengeId: ChallengeId) : DomainEvent()

typealias ChallengerId = PlayerId
typealias OpponentId = PlayerId
typealias PlayerId = UUID
typealias PlayerNickname = String

@JvmInline value class ChallengeId private constructor(val id: String) {

    companion object {
        private val hashIds = Hashids("ScrambledEggs Challenges Salt")
        fun newChallengeId(): ChallengeId = ChallengeId(hashIds.next())
        fun challengeId(id: String) = ChallengeId(id)
    }
}

enum class GameMode {
    Duel, TwoVsTwo, WipeOut, CTF
}