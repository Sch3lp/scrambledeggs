package org.scrambled.infra.domainevents

import org.slf4j.LoggerFactory

class InMemoryDomainEventBroadcaster : IDomainEventBroadcaster {

    private val logger = LoggerFactory.getLogger(InMemoryDomainEventBroadcaster::class.java)

    private val events: MutableList<DomainEvent> = mutableListOf()

    override fun publish(domainEvent: DomainEvent) {
        events += domainEvent
        logger.info("$domainEvent was broadcast")
    }
    fun <T> findEvent(clazz: Class<T>): T? {
        return events.filterIsInstance(clazz).firstOrNull()
    }
}
