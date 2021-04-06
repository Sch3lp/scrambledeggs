package org.scrambled.scenariotests.steps.leaderboard

import io.ktor.client.request.*
import io.ktor.http.*
import org.scrambled.adapter.restapi.leaderboards.LeaderboardEntryJson
import org.scrambled.scenariotests.steps.client.baseUrl
import org.scrambled.scenariotests.steps.client.client

suspend fun fetchLeaderboardStep(): List<LeaderboardEntryJson> = client.get {
    url("$baseUrl/leaderboard")
    contentType(ContentType.Application.Json)
}

suspend fun triggerLeaderboardRehydration() {
    client.post<Any> {
        url("$baseUrl/leaderboard/rehydrate")
        contentType(ContentType.Application.Json)
    }
}