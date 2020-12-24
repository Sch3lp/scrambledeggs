package org.scrambled.adapter.eventsourcing.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.runBlocking
import org.scrambled.adapter.eventsourcing.extensions.scrambledObjectMapper
import java.time.LocalDateTime
import java.util.*

enum class EventType {
     PlayerRegistered
}

sealed class Event(val type: EventType) {
    val id: UUID = UUID.randomUUID()
    val at: LocalDateTime = LocalDateTime.now()
    data class PlayerRegistered(val nickname: String): Event(EventType.PlayerRegistered)
}

fun Event.asJson() = scrambledObjectMapper().writeValueAsString(this)
fun Event.asBlocking() = runBlocking { asJson() }



interface EventStore : Flow<Event> {
    suspend fun push(event: Event)
}

//suspend inline fun <reified T : Event> EventStore.lastEventOrNull() = toList().lastOrNull { it is T } as T?
//needs to be used differently: .collectLatest { ... }
suspend inline fun <reified T : Event> EventStore.filterEvents() = filterIsInstance<T>()

