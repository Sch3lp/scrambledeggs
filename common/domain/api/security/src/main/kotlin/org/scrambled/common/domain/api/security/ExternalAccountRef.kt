package org.scrambled.common.domain.api.security

typealias JwtIss = String
typealias JwtSub = String

data class ExternalAccountRef(
    val jwtIss: JwtIss, val jwtSub: JwtSub
)
