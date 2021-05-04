package org.scrambled.adapter.restapi.players

import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.PlayerByExternalAccountRef
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(
    "/api/player",
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class PlayerController(
    private val queryExecutor: QueryExecutor,
) {

    @GetMapping(path = ["/{id}"])
    fun getPlayerById(
        @PathVariable id: PlayerId
    ): ResponseEntity<RegisteredPlayerJson> {
        val player = queryExecutor.execute(PlayerById(id), RegisteredPlayerRepresentation::toJson)

        return ResponseEntity.ok(player)
    }

    @GetMapping(params = ["jwtIss", "jwtSub"])
    fun getPlayerByExternalAccountRef(
        @RequestParam("jwtIss") jwtIss: JwtIss,
        @RequestParam("jwtSub") jwtSub: JwtSub
    ): ResponseEntity<List<RegisteredPlayerJson>> {
        val externalAccountRef = ExternalAccountRef(jwtIss, jwtSub)
        val player = queryExecutor.executeOrNull(
            PlayerByExternalAccountRef(externalAccountRef),
            RegisteredPlayerRepresentation::toJson
        )
        val possiblyEmptyPlayers = player?.let { listOf(it) } ?: listOf()
        return ResponseEntity.ok(possiblyEmptyPlayers)
    }

    @GetMapping
    fun getPlayers(): ResponseEntity<List<RegisteredPlayerJson>> {
        val players: List<RegisteredPlayerJson> = queryExecutor.execute(FetchAllRegisteredPlayers) {
            this.map(RegisteredPlayerRepresentation::toJson)
        }

        return ResponseEntity.ok(players)
    }

}

data class RegisteredPlayerJson(val playerId: UUID, val nickname: String)

internal fun RegisteredPlayerRepresentation.toJson(): RegisteredPlayerJson =
    RegisteredPlayerJson(this.id, this.nickname)

