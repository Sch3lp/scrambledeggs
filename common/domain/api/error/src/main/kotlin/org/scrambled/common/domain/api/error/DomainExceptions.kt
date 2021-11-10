package org.scrambled.common.domain.api.error

open class DomainRuntimeException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
class NotFoundException(message: String) : DomainRuntimeException(message)
class NotValidException(message: String) : DomainRuntimeException(message)