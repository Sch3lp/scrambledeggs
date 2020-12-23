package org.scrambled.adapter.eventsourcing.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.toList
import java.time.LocalDateTime
import java.util.*

enum class EventType {
     PlayerRegistered
}

sealed class Event(val type: EventType) {
    val id: UUID = UUID.randomUUID()
    val at: LocalDateTime = LocalDateTime.now()
    object PlayerRegistered: Event(EventType.PlayerRegistered)
}

interface EventStore : Flow<Event> {
    suspend fun push(event: Event)
}

//suspend inline fun <reified T : Event> EventStore.lastEventOrNull() = toList().lastOrNull { it is T } as T?
//needs to be used differently: .collectLatest { ... }
suspend inline fun <reified T : Event> EventStore.filterEvents() = filterIsInstance<T>()