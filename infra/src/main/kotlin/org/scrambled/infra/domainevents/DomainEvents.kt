package org.scrambled.infra.domainevents

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

typealias DomainEventId = UUID
abstract class DomainEvent {
    val id: DomainEventId = UUID.randomUUID()
    val time: LocalDateTime = LocalDateTime.now()
    override fun toString(): String {
        return this::javaClass.name
    }
}

interface IDomainEventBroadcaster {
    fun publish(domainEvent: DomainEvent)
}

@Component
class SpringEventsDomainEventBroadcaster(
    private val publisher: ApplicationEventPublisher
) : IDomainEventBroadcaster {
    private val logger = LoggerFactory.getLogger(SpringEventsDomainEventBroadcaster::class.java)

    override fun publish(domainEvent: DomainEvent) {
        publisher.publishEvent(domainEvent)
        logger.info("Broadcasted: $domainEvent")
    }

}
