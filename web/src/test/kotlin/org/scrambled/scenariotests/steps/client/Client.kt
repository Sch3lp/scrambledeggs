package org.scrambled.scenariotests.steps.client

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions

val client = HttpClient(CIO) {
    install(JsonFeature) {
        serializer = JacksonSerializer {
            registerModules(KotlinModule(), JavaTimeModule())
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
}
val baseUrl = "http://localhost:9999/api"

sealed class ApiResult<T> {
    fun wasSuccess(): Boolean = this is Success

    data class Success<T>(val value: T) : ApiResult<T>()
    data class ApiError<T>(val response: HttpResponse) : ApiResult<T>() {
        suspend inline fun <reified E> receiveBody(): E = response.receive()
    }

    fun expectSuccess(): T =
        when (this) {
            is Success<T> -> {
                this.value
            }
            is ApiError<T> -> {
                val that = this
                val errorMessage = runBlocking { that.receiveBody<String>() }
                Assertions.fail("Expected success, but got $errorMessage")
            }
        }

    fun expectFailure(): String =
        when (this) {
            is ApiError<T> -> {
                val that = this
                runBlocking { that.receiveBody() }
            }
            is Success<T> -> {
                Assertions.fail("Expected ƒailure, but got ${this.value}")
            }
        }
}

fun <T> Any?.asApiResult(response: HttpResponse): ApiResult<T> =
    if (this != null) {
        ApiResult.Success(this as T)
    } else ApiResult.ApiError(
        response
    )
