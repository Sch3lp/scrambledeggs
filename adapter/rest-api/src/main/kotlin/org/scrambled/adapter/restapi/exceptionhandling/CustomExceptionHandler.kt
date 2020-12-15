package org.scrambled.adapter.restapi.exceptionhandling

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


//TODO move to core-api at some point (when we've got actual exceptions)
class CustomException(override val message: String?): RuntimeException(message)
fun CustomException.toResponse(): ResponseEntity<String> = ResponseEntity.badRequest().body(this.message)

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(value = [CustomException::class])
    fun handleCustomException(exception: CustomException) = exception.toResponse()
}


