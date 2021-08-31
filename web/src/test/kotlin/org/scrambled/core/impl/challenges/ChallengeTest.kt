package org.scrambled.core.impl.challenges

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.scrambled.domain.core.api.exceptions.NotValidException
import java.util.*

class ChallengeTest {

    @Test
    fun `Cannot create a Challenge to yourself`() {
        val challengerId = UUID.randomUUID()

        assertThatExceptionOfType(NotValidException::class.java)
            .isThrownBy { Challenge.createChallenge(challengerId, challengerId, "comment", "some day") }
            .withMessage("You cannot challenge yourself.")
    }

    @ParameterizedTest
    @ValueSource(strings = [""," ", "", "\t", "\n", "  "])
    fun `Cannot create a Challenge without a comment or appointment suggestion`(comment: String) {
        assertThatExceptionOfType(NotValidException::class.java)
            .isThrownBy { Challenge.createChallenge(UUID.randomUUID(), UUID.randomUUID(), comment, "asdfa") }
            .withMessage("You cannot challenge without a useful comment or appointment suggestion.")
    }

    @ParameterizedTest
    @ValueSource(strings = [""," ", "", "\t", "\n", "  "])
    fun `Cannot create a Challenge without an appointment suggestion`(appointmentSuggestion: String) {
        assertThatExceptionOfType(NotValidException::class.java)
            .isThrownBy { Challenge.createChallenge(UUID.randomUUID(), UUID.randomUUID(), "asdfa", appointmentSuggestion) }
            .withMessage("You cannot challenge without a useful comment or appointment suggestion.")
    }
}