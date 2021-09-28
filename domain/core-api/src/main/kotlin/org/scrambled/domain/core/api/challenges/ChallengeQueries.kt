package org.scrambled.domain.core.api.challenges

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.infra.cqrs.AggregateId
import org.scrambled.infra.cqrs.Query
import java.util.*

data class PendingChallengesFor(val challengedPlayerId: PlayerId): Query<List<PendingChallengeRepresentation>> {
    override val id: AggregateId
        get() = UUID.randomUUID()
}

data class PendingChallengeRepresentation(
    val challengeId: ChallengeId,
    val gameMode: GameMode,
    val opponentName: UsefulString,
    val appointment: UsefulString,
)
