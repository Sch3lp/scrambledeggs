package org.scrambled.common.adapter.restapi.exceptionhandling

import org.scrambled.common.domain.api.error.DomainRuntimeException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


fun DomainRuntimeException.toResponse(): ResponseEntity<String> = ResponseEntity.badRequest().body(this.message)

@RestControllerAdvice
class CustomExceptionHandler {
    private val logger = LoggerFactory.getLogger(CustomExceptionHandler::class.java)

    @ExceptionHandler(value = [DomainRuntimeException::class])
    fun handleCustomException(exception: DomainRuntimeException) = exception
        .also { logger.error("Error!", exception) }
        .toResponse()
}


