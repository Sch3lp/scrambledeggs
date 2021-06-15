package org.scrambled.scenariotests.steps.core

import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.junit.jupiter.api.fail
import org.scrambled.adapter.restapi.players.RegisterPlayerJson
import org.scrambled.adapter.restapi.players.RegisteredPlayerJson
import org.scrambled.domain.core.api.challenging.PlayerId
import org.scrambled.domain.core.api.extensions.toPlayerId
import org.scrambled.domain.core.api.registration.JwtIss
import org.scrambled.domain.core.api.registration.JwtSub
import org.scrambled.scenariotests.steps.client.ApiResult
import org.scrambled.scenariotests.steps.client.asApiResult
import org.scrambled.scenariotests.steps.client.baseUrl
import org.scrambled.scenariotests.steps.client.client
import java.util.*

data class JwtInfo(val jwtIss: JwtIss, val jwtSub: JwtSub)

suspend fun registerPlayerStep(playerNickname: String, jwtInfo: JwtInfo): ApiResult<PlayerId> {
    val response = client.post<HttpResponse> {
        url("$baseUrl/register")
        contentType(ContentType.Application.Json)
        header(HttpHeaders.Authorization, "Bearer ${jwtInfo.asDummyEncodedJwt()}")
        body = RegisterPlayerJson(playerNickname, jwtInfo.jwtIss, jwtInfo.jwtSub)
        expectSuccess = false
    }
    val locationHeader = response.headers["Location"]
    return locationHeader?.takeLast(36)?.toPlayerId().asApiResult(response)
}

suspend fun fetchPlayerByJwtInfoStep(jwtInfo: JwtInfo): RegisteredPlayerJson? {
    val players: List<RegisteredPlayerJson> = client.get {
        url("$baseUrl/player/info")
        contentType(ContentType.Application.Json)
        header(HttpHeaders.Authorization, "Bearer ${jwtInfo.asDummyEncodedJwt()}")
    }
    return players.firstOrNull()
}

suspend fun fetchPlayerStep(playerId: PlayerId): RegisteredPlayerJson {
    return client.get {
        url("$baseUrl/player/$playerId")
        contentType(ContentType.Application.Json)
    }
}

suspend fun fetchAllPlayersStep(): List<RegisteredPlayerJson> {
    return client.get {
        url("$baseUrl/player")
        contentType(ContentType.Application.Json)
    }
}

private fun JwtInfo.asDummyEncodedJwt(): String = dummyJwt(this).encoded

private fun dummyJwt(jwtInfo: JwtInfo): Jwt = """
{
"exp": 1620161054,
"iat": 1620160154,
"auth_time": 1620159340,
"jti": "24de27a4-3506-4771-89f2-fc4dbef682c5",
"iss": "${jwtInfo.jwtIss}",
"aud": "account",
"sub": "${jwtInfo.jwtSub}",
"typ": "Bearer",
"azp": "scrambled-ui",
"nonce": "1234",
"session_state": "36cca453-94d5-41a0-b9a4-366efce7f77a",
"acr": "0",
"allowed-origins": [
"http://localhost:8000"
],
"realm_access": {
"roles": [
  "offline_access",
  "uma_authorization"
]
},
"resource_access": {
"account": {
  "roles": [
    "manage-account",
    "manage-account-links",
    "view-profile"
  ]
}
},
"scope": "openid email profile",
"email_verified": false,
"name": "Tim",
"preferred_username": "tim",
"given_name": "Tim"
}
"""
    .trimIndent()
    .asJwt()

fun String.asJwt(): Jwt = Jwt(this)

data class Jwt(private val _decoded: String) {
    val encoded: String
        get() {
            val header = Base64.getEncoder().encodeToString(_jwtHeader.encodeToByteArray())
            val payload = Base64.getEncoder().encodeToString(_decoded.encodeToByteArray())
            val signature = "YgCtwZJQ0HCkMX_nMJZ68ninW6UBRAey_vhsveFImieqcbM428XbNEqAEDCZRSWv3AfogxZWaVNTBNgDefl2gxThMByv00ondcWl_0zLlMXCqV3_ahMIXFyx-UxtIAlWpWATleUuwQY-JGIS_VB7th9fqvcJfjocZjJJVhq3L3HCXNr22azIsXJRY_YoXNZXPoSSRUQwplYaHkjWtYsgXm8NhHh9K8UbRYuTQ8ljwKc58IGLxG_EojeiDB9IWre67ah1Q6y4uMs0PD2HLBF7SfOJtHHyyRFepqOtEF8p2ClDuW96GNNeejLy1i8a9w5bIGzsLBu08yAsYc8W9E4BWg"
            return listOf(header, payload, signature)
                .joinToString(".")
        }


    private val _jwtHeader = """
{
  "alg": "RS256",
  "typ": "JWT",
  "kid": "XnC2y_Bb-zh7i8oKjb-YNKlt5iU6RBjhLAFO8MZuIhQ"
}
""".trimIndent()
}
