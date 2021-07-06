package org.scrambled.adapter.restapi.players

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.scrambled.adapter.restapi.extensions.toExternalAccountRef
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.PlayerByExternalAccountRef
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @GetMapping("/info")
    fun getPlayerByExternalAccountRef(): ResponseEntity<List<RegisteredPlayerJson>> {
        val externalAccountRef = SecurityContextHolder.getContext().toExternalAccountRef()
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class Jwt(val iss: JwtIss, val sub: JwtSub)