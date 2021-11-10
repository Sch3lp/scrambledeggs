package org.scrambled.matches.domain.core.challenges

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.GameMode
import org.scrambled.common.domain.api.error.NotValidException
import java.util.*

class PendingChallengeTest {

    @Test
    fun `Cannot create a Challenge to yourself`() {
        val challengerId = UUID.randomUUID()

        assertThatExceptionOfType(NotValidException::class.java)
            .isThrownBy {
                PendingChallenge.createChallenge(
                    challengerId,
                    challengerId,
                    UsefulString("comment"),
                    UsefulString("some day"),
                    GameMode.CTF
                )
            }
            .withMessage("You cannot challenge yourself.")
    }
}