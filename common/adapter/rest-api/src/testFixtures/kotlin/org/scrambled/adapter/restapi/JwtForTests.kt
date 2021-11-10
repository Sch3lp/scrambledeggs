package org.scrambled.adapter.restapi

import org.scrambled.common.domain.api.security.JwtIss
import org.scrambled.common.domain.api.security.JwtSub
import java.util.*


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
internal fun String.asJwt(): Jwt = Jwt(this)

data class JwtInfo(private val jwtIss: JwtIss, val jwtSub: JwtSub) {

    fun asDummyEncodedJwt(): String = dummyJwt().encoded

    private fun dummyJwt(): Jwt = """
    {
    "exp": 1620161054,
    "iat": 1620160154,
    "auth_time": 1620159340,
    "jti": "24de27a4-3506-4771-89f2-fc4dbef682c5",
    "iss": "$jwtIss",
    "aud": "account",
    "sub": "$jwtSub",
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
    "name": "$jwtSub",
    "preferred_username": "$jwtSub",
    "given_name": "$jwtSub"
    }
    """
        .trimIndent()
        .asJwt()
}