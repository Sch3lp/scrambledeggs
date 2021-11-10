package org.scrambled.domain.core.api

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.scrambled.domain.api.error.NotValidException


class UsefulStringTest {
    @ParameterizedTest
    @ValueSource(strings = [""," ", "", "\t", "\n", "  "])
    fun `Cannot create a UsefulString with unuseful input`(aString: String) {
        Assertions.assertThatExceptionOfType(NotValidException::class.java)
            .isThrownBy { UsefulString(aString) }
            .withMessage("This string is not useful.")
    }
}