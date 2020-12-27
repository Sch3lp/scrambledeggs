package org.scrambled.adapter.eventsourcing.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.runBlocking
import org.scrambled.adapter.eventsourcing.extensions.scrambledObjectMapper
import java.time.LocalDateTime
import java.util.*

enum class EventType {
    PlayerRegistered,
    PlayerRenamed
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
sealed class Event(val type: EventType) {
    val id: UUID = UUID.randomUUID()
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val at: LocalDateTime = LocalDateTime.now()

    @JsonTypeName("PlayerRegistered")
    data class PlayerRegistered(val nickname: String) : Event(EventType.PlayerRegistered)

    @JsonTypeName("PlayerRenamed")
    data class PlayerRenamed(val newNickname: String) : Event(EventType.PlayerRenamed)
}

fun Event.asJson() = scrambledObjectMapper().writeValueAsString(this)
fun Event.asJsonBlocking() = runBlocking { asJson() }
inline fun <reified T> String.fromJson(): T = scrambledObjectMapper().readValue(this)


interface EventStore : Flow<Event> {
    suspend fun push(event: Event)
}

//suspend inline fun <reified T : Event> EventStore.lastEventOrNull() = toList().lastOrNull { it is T } as T?
//needs to be used differently: .collectLatest { ... }
suspend inline fun <reified T : Event> EventStore.filterEvents() = filterIsInstance<T>()

