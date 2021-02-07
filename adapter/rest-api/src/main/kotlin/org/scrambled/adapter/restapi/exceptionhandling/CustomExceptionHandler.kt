package org.scrambled.adapter.restapi.exceptionhandling

import org.scrambled.domain.core.api.exceptions.DomainRuntimeException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


fun DomainRuntimeException.toResponse(): ResponseEntity<String> = ResponseEntity.badRequest().body(this.message)

@RestControllerAdvice
class CustomExceptionHandler {
    @ExceptionHandler(value = [DomainRuntimeException::class])
    fun handleCustomException(exception: DomainRuntimeException) = exception.toResponse()
}


