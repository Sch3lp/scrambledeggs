package org.scrambled.domain.core.api

import org.scrambled.domain.core.api.exceptions.NotValidException

@JvmInline
value class UsefulString(private val _s: String) {

    val value: String
        get() = _s

    init {
        validateUsefulString(_s)
    }

    private fun validateUsefulString(s: String) {
        if (s.isBlank()) {
            throw NotValidException("This string is not useful.")
        }
    }

}