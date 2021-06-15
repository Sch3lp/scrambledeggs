package org.scrambled.adapter.restapi.extensions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.scrambled.adapter.restapi.players.Jwt
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import java.util.*

internal fun String.toExternalAccountRef(): ExternalAccountRef {
    val encodedBearerToken = this.substringAfter("Bearer ").split(".")
    val jwtPayload = encodedBearerToken[1]
    val decodedJwt = Base64.getUrlDecoder().decode(jwtPayload).decodeToString()
    val jwt: Jwt = jacksonObjectMapper().readValue(decodedJwt)
    return ExternalAccountRef(jwt.iss, jwt.sub)
}
