package org.scrambled.leaderboards.domain.core.infra

import org.scrambled.matches.domain.api.challenges.PlayerChallenged
import org.scrambled.matches.domain.api.registration.PlayerRegistered
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvent
import org.scrambled.leaderboards.domain.api.infra.BroadcastEvents
import org.scrambled.infra.domainevents.DomainEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DomainEventToBroadcastEventMapper(
    private val broadcastEvents: BroadcastEvents
) {
    private val logger = LoggerFactory.getLogger(DomainEventToBroadcastEventMapper::class.java)

    @EventListener
    fun <E : DomainEvent> on(domainEvent: E) =
        mapToBroadcastEvent(domainEvent)?.store()

    private fun <E : DomainEvent> mapToBroadcastEvent(domainEvent: E) = when (domainEvent) {
        is PlayerRegistered -> BroadcastEvent.PlayerRegisteredForLeaderboard(
            domainEvent.playerId,
            domainEvent.nickName
        )
        is PlayerChallenged -> BroadcastEvent.PlayerChallengedForLeaderboard(
            domainEvent.challenger,
            domainEvent.opponent
        )
        else -> null.also { logger.info("Map $domainEvent to a BroadcastEvent") } //no throw cuz it changes implicit return type to Any
    }

    private fun BroadcastEvent.store() = broadcastEvents.keep(this)
}