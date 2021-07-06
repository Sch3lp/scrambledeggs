package org.scrambled.adapter.restapi.security

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest


private const val JwtCookieName = "CRUMBLE"

@EnableWebSecurity
@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity?) {
        http {
            authorizeRequests {
                authorize("/api/**", authenticated)
                authorize(anyRequest, anonymous)
            }
            oauth2ResourceServer {
                jwt {
                    bearerTokenResolver = CookieBearerTokenResolver()
                }
            }
        }
    }

    class CookieBearerTokenResolver : BearerTokenResolver {
        override fun resolve(request: HttpServletRequest?): String? =
            request?.cookies?.firstOrNull { it.name == JwtCookieName }?.value
                ?: DefaultBearerTokenResolver().resolve(request)
    }
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
            .secure(true)
            .path("/")
            .maxAge(6000) //TODO: align with JWT ttl?
            .domain("localhost")
            .build()
        return ResponseEntity.accepted()
            .header(HttpHeaders.SET_COOKIE, crumble.toString())
            .build()
    }
}
//JwtAuthenticationProvider
//We might need to set up/configure a `AuthenticationEntryPoint` to return a 401 (unauthorized) response when the UI sends ILLEGAL JWT on the cookie.
//We also might need to set up/configure a `AuthenticationEntryPoint` to not go off on a valid JWT
//I think this because I read "Spring Security typically requests the credentials using AuthenticationEntryPoint"
//seems interesting: https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#oauth2login-advanced-idtoken-verify

//We might need a specific BearerTokenAuthenticationFilter that extracts the Bearer token out of a cookie

// iss: http://localhost:7070/auth/realms/scrambled
//      http://localhost:7070/auth/realms/scrambled/.well-known/openid-configuration
// this might have impact on whether or not we can use issuer-uri as a resourceserver springboot config

//what we need to do:
// ✅ configure our rest-api to be a resourceserver
//  configure the properties for that (maybe we need manual config if issuer-uri doesn't magically work, for that see https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#oauth2resourceserver-jwt-jwkseturi )
//  customize a `BearerTokenResolver` to extract the JWT out of a cookie, instead of expecting it in the `Authorization: Bearer` header
//  see https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#oauth2resourceserver-bearertoken-resolver

//if we ever get to adding roles into scrambled, we might use a `CustomAuthenticationConverter` which gets a RegisteredPlayer based on the JWT.iss+sub
//and understands how to map roles we keep internally to GrantedAuthorities
//see https://docs.spring.io/spring-security/site/docs/5.4.1/reference/html5/#oauth2resourceserver-jwt-authorization-extraction