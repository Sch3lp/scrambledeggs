package org.scrambled.adapter.eventsourcing.eventstore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.scrambled.adapter.eventsourcing.api.Event
import org.scrambled.adapter.eventsourcing.api.EventStore

class InMemoryEventStore(private val _events: MutableList<Event> = emptyList<Event>().toMutableList()) :
    Flow<Event> by _events.asFlow(), EventStore {

    override suspend fun push(event: CoroutineScope) {
        _events.add(event)
    }
}