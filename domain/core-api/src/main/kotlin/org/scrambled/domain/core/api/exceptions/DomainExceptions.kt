package org.scrambled.domain.core.api.exceptions

open class DomainRuntimeException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
class NotFoundException(message: String?) : DomainRuntimeException(message)
class NotValidException(message: String) : DomainRuntimeException(message)