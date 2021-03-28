package org.scrambled.ktorapi.koin

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.ext.modules
import org.scrambled.ktorapi.players.registration.playerRoutes
import org.scrambled.ktorapi.players.registration.registrationRoutes

private fun createKtorApiModule(): org.koin.core.module.Module = module(createdAtStart = true) {
    single { jacksonObjectMapper().registerModule(JavaTimeModule()) }
}

fun Application.ktorApi() {

    modules(createKtorApiModule())

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(get()))
    }

    routing {
        registrationRoutes()
        playerRoutes()
    }
}