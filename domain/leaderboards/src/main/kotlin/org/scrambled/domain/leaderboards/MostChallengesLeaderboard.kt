package org.scrambled.domain.leaderboards

import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.infra.cqrs.DomainEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

class MostChallengesLeaderboard {
}


@Component
class MostChallengesLeaderboardPolicy(
    private val leaderboardEvents: LeaderboardEvents
) {
    @EventListener
    fun <E : DomainEvent> on(domainEvent: E) =
        when (domainEvent) {
            is PlayerRegistered -> LeaderboardEvent.PlayerRegisteredForLeaderboard(
                domainEvent.playerId,
                domainEvent.nickName
            )
            else -> null
        }?.store()


    fun LeaderboardEvent.store() = leaderboardEvents.keep(this)
}

