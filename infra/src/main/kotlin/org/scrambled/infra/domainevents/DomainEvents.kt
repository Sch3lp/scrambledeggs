package org.scrambled.infra.cqrs

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

//class DomainApplicationEvent<E: DomainEvent>(val domainEvent: E)

interface IDomainEventBroadcaster {
    fun publish(domainEvent: DomainEvent)
}

interface IDomainEventSubscriber {
    fun <E: DomainEvent> on(domainEvent: E)
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

// No @Component because should only be used in tests
// Too lazy to move to shared-test package and create shared-test gradle configuration atm
class InMemoryDomainEventBroadcaster : IDomainEventBroadcaster {

    private val logger = LoggerFactory.getLogger(InMemoryDomainEventBroadcaster::class.java)

    private val events: MutableList<DomainEvent> = mutableListOf()

    override fun publish(domainEvent: DomainEvent) {
        events += domainEvent
        logger.info("$domainEvent was broadcast")
    }
    fun <T> InMemoryDomainEventBroadcaster.findEvent(clazz: Class<T>): T? {
        return events.filterIsInstance(clazz).firstOrNull()
    }
}
