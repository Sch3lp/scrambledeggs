package org.scrambled.adapter.restapi.extensions

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.scrambled.adapter.restapi.players.Jwt
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.*

internal fun String.toExternalAccountRef(): ExternalAccountRef {
    val encodedBearerToken = this.substringAfter("Bearer ").split(".")
    val jwtPayload = encodedBearerToken[1]
    val decodedJwt = Base64.getUrlDecoder().decode(jwtPayload).decodeToString()
    val jwt: Jwt = jacksonObjectMapper().readValue(decodedJwt)
    return ExternalAccountRef(jwt.iss, jwt.sub)
}

internal fun SecurityContext.toExternalAccountRef(): ExternalAccountRef {
    val jwt = (this.authentication as JwtAuthenticationToken).token
    return ExternalAccountRef(jwt.issuer.toString(), jwt.subject)
}
