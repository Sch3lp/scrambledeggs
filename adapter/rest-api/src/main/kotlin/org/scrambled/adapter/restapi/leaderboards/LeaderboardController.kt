package org.scrambled.adapter.restapi.leaderboards

import org.scrambled.domain.leaderboards.api.mostchallengesdone.projections.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LeaderboardEntryJson(
    val rank: Rank?,
    val nickname: Nickname,
    val score: Score,
)

@RestController
@RequestMapping(
    "/api/leaderboard",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class LeaderboardController(
    private val leaderboard: MostChallengesDoneLeaderboardProjection
) {
    @GetMapping
    fun get(): ResponseEntity<List<LeaderboardEntryJson>> {
        return ResponseEntity.ok(leaderboard.getRanking().asJson())
    }
}

fun List<ProjectedPlayer>.asJson() : List<LeaderboardEntryJson> = this.map {
    LeaderboardEntryJson(it.rank, it.nickname, it.score)
}