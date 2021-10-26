package org.scrambled.domain.core.api

import org.scrambled.domain.core.api.exceptions.NotValidException

@JvmInline
value class UsefulString(val value: String) {

    init {
        validateUsefulString(value)
    }

    private fun validateUsefulString(s: String) {
        if (s.isBlank()) {
            throw NotValidException("This string is not useful.")
        }
    }

}