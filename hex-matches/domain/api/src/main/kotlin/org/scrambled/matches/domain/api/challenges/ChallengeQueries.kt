package org.scrambled.matches.domain.api.challenges

import org.scrambled.infra.cqrs.Query

data class PendingChallengesFor(val challengedPlayerId: PlayerId): Query<List<QueryablePendingChallenge>>
data class PendingChallengeById(val challengeId: ChallengeId): Query<QueryablePendingChallenge>


