package org.scrambled.domain.core.api.challenges

import org.scrambled.infra.cqrs.Query

data class PendingChallengesFor(val challengedPlayerId: PlayerId): Query<List<QueryablePendingChallenge>>
