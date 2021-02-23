package org.scrambled.domain.leaderboards.impl.infra

import org.scrambled.domain.core.api.registration.PlayerRegistered
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvent
import org.scrambled.domain.leaderboards.api.infra.BroadcastEvents
import org.scrambled.infra.cqrs.DomainEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DomainEventToBroadcastEventMapper(
    private val broadcastEvents: BroadcastEvents
) {
    @EventListener
    fun <E : DomainEvent> on(domainEvent: E) =
        mapToBroadcastEvent(domainEvent)?.store()

    private fun <E : DomainEvent> mapToBroadcastEvent(domainEvent: E) = when (domainEvent) {
        is PlayerRegistered -> BroadcastEvent.PlayerRegisteredForLeaderboard(
            domainEvent.playerId,
            domainEvent.nickName
        )
        else -> null
    }

    private fun BroadcastEvent.store() = broadcastEvents.keep(this)
}