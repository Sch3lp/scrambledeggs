package org.scrambled.leaderboards.adapters.`in`.rest.leaderboards

import org.scrambled.infra.cqrs.CommandExecutor
import org.scrambled.leaderboards.domain.api.mostchallengesdone.projections.*
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LeaderboardEntryJson(
    val rank: Rank?,
    val nickname: Nickname,
    val score: Score,
    val playerId: PlayerId,
)

@RestController
@RequestMapping(
    "/api/leaderboard",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class LeaderboardController(
    private val leaderboard: MostChallengesDoneLeaderboardProjection,
    private val commandExecutor: CommandExecutor,
) {
    @GetMapping
    fun get(): ResponseEntity<List<LeaderboardEntryJson>> {
        return ResponseEntity.ok(leaderboard.getRanking().asJson())
    }

    @PostMapping("/rehydrate")
    fun rehydrateLeaderboard(): ResponseEntity<Any> {
        commandExecutor.execute(RehydrateLeaderboards)
        return ResponseEntity.noContent().build()
    }
}

fun List<ProjectedPlayer>.asJson() : List<LeaderboardEntryJson> = this.map {
    LeaderboardEntryJson(it.rank, it.nickname, it.score, it.playerId)
}