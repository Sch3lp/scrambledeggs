package org.scrambled.adapter.restapi.security

import org.scrambled.adapter.restapi.extensions.removeBearer
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


private const val JwtCookieName = "CRUMBLE"

open class CookieBearerTokenResolver : BearerTokenResolver {
    override fun resolve(request: HttpServletRequest?): String? =
        request?.cookies?.firstOrNull { it.name == JwtCookieName }?.value
            ?: DefaultBearerTokenResolver().resolve(request)
}

class CookieBearerTokenResolverForTest : CookieBearerTokenResolver() {
    override fun resolve(request: HttpServletRequest?): String? =
        request?.cookies?.firstOrNull { it.name == JwtCookieName }?.value
            ?: request?.getHeader(HttpHeaders.AUTHORIZATION)?.removeBearer()
}

@RestController
@RequestMapping("/api/session")
class SessionController() {
    @GetMapping
    fun exchangeAuthHeaderForCookie(): ResponseEntity<Any> {
        //TODO: maybe make this more explicit with types at least?
        val jwtAsString = (SecurityContextHolder.getContext().authentication as JwtAuthenticationToken).token.tokenValue
        val crumble = ResponseCookie.from(JwtCookieName, jwtAsString)
            .httpOnly(true)
            .secure(false) //TODO: probably set this to true if we're hosting on https
            .path("/")
            .maxAge(6000) //TODO: align with JWT ttl?
            .domain("localhost")
            .build()
        return ResponseEntity.accepted()
            .header(HttpHeaders.SET_COOKIE, crumble.toString())
            .build()
    }
}
