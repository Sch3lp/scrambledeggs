package org.scrambled.core.impl.challenges

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
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
}