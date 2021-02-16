package org.scrambled.domain.leaderboards.api

import java.time.LocalDateTime
import java.util.*


typealias BroadcastEventId = UUID
sealed class BroadcastEvent {
    val id: BroadcastEventId = UUID.randomUUID()
    val time: LocalDateTime = LocalDateTime.now()
    data class PlayerRegisteredForLeaderboard(val playerId: UUID, val nickname: String): BroadcastEvent()
}

interface BroadcastEvents {
    fun keep(event: BroadcastEvent)
}