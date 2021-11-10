package org.scrambled.scenariotests.steps.leaderboard

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.scrambled.leaderboards.adapters.`in`.rest.leaderboards.LeaderboardEntryJson
import org.scrambled.scenariotests.steps.client.baseUrl

suspend fun HttpClient.fetchLeaderboardStep(): List<LeaderboardEntryJson> =
    this.get {
        url("$baseUrl/leaderboard")
        contentType(ContentType.Application.Json)
    }

suspend fun HttpClient.triggerLeaderboardRehydration() {
    this.post<Any> {
        url("$baseUrl/leaderboard/rehydrate")
        contentType(ContentType.Application.Json)
    }
}