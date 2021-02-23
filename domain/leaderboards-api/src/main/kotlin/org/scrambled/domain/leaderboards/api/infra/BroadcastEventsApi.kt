package org.scrambled.domain.leaderboards.api.infra

import java.time.LocalDateTime
import java.util.*


typealias BroadcastEventId = UUID
sealed class BroadcastEvent {
    val id: BroadcastEventId = UUID.randomUUID()
    val time: LocalDateTime = LocalDateTime.now()
    data class PlayerRegisteredForLeaderboard(val playerId: UUID, val nickname: String): BroadcastEvent()
    data class PlayerChallengedForLeaderboard(val initiator: UUID, val opponent: UUID): BroadcastEvent()
}

interface BroadcastEvents {
    fun keep(event: BroadcastEvent)
    fun findAll(): List<BroadcastEvent>
}