package org.scrambled.domain.leaderboards.api

import java.time.LocalDateTime
import java.util.*


typealias LeaderboardEventId = UUID
sealed class LeaderboardEvent {
    val id: LeaderboardEventId = UUID.randomUUID()
    val time: LocalDateTime = LocalDateTime.now()
    data class PlayerRegisteredForLeaderboard(val playerId: UUID, val nickname: String): LeaderboardEvent()
}

interface LeaderboardEvents {
    fun keep(event: LeaderboardEvent)
}