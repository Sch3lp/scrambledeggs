package org.scrambled.domain.core.api.exceptions

open class DomainRuntimeException(message: String?) : RuntimeException(message)
class NotFoundException(message: String?) : DomainRuntimeException(message)