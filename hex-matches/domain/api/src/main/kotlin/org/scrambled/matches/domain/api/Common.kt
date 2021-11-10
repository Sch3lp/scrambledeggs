package org.scrambled.matches.domain.api

import org.scrambled.common.domain.api.error.NotValidException

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