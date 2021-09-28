package org.scrambled.domain.core.api.challenges

import org.scrambled.domain.core.api.UsefulString
import org.scrambled.infra.cqrs.Query

data class PendingChallengesFor(val challengedPlayerId: PlayerId): Query<List<PendingChallengeRepresentation>>

data class PendingChallengeRepresentation(
    val challengeId: ChallengeId,
    val gameMode: GameMode,
    val opponentName: UsefulString,
    val appointment: UsefulString,
)
