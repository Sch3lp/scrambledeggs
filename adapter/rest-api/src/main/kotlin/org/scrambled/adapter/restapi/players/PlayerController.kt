package org.scrambled.adapter.restapi.players

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.players.FetchAllRegisteredPlayers
import org.scrambled.domain.core.api.players.PlayerByExternalAccountRef
import org.scrambled.domain.core.api.players.PlayerById
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.infra.cqrs.QueryExecutor
import org.springframework.http.HttpHeaders
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

    @GetMapping("/info")
    fun getPlayerByExternalAccountRef(@RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String): ResponseEntity<List<RegisteredPlayerJson>> {
        val encodedBearerToken = authHeader.substringAfter("Bearer ").split(".")
        val jwtPayload = encodedBearerToken[1]
        val decodedJwt = Base64.getUrlDecoder().decode(jwtPayload).decodeToString()
        val jwt: Jwt = jacksonObjectMapper().readValue(decodedJwt)
        val externalAccountRef = ExternalAccountRef(jwt.iss, jwt.sub)
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