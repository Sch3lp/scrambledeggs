package org.scrambled.matches.domain.core.players

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.scrambled.common.domain.api.security.ExternalAccountRef
import org.scrambled.matches.domain.api.UsefulString
import org.scrambled.matches.domain.api.challenges.ChallengeId
import org.scrambled.matches.domain.api.challenges.GameMode
import org.scrambled.matches.domain.core.challenges.AcceptedChallenge
import org.scrambled.matches.domain.core.challenges.PendingChallenge
import java.util.*


class RegisteredPlayerTest {

    @Test
    fun `You cannot accept a pending challenge you created yourself`() {
        val challenger = RegisteredPlayer(UUID.randomUUID(), "Sch3lp", ExternalAccountRef("iss", "123"))
        val opponent = RegisteredPlayer(UUID.randomUUID(), "MrRat", ExternalAccountRef("iss", "456"))

        val pendingChallenge = challenger.challenge(opponent)

        assertThatExceptionOfType(SelfAcceptException::class.java)
            .isThrownBy { challenger.accept(pendingChallenge) }
    }

    @Test
    fun `Accept a pending challenge you did not create yourself, returns an AcceptedChallenge`() {
        val challenger = RegisteredPlayer(UUID.randomUUID(), "Sch3lp", ExternalAccountRef("iss", "123"))
        val opponent = RegisteredPlayer(UUID.randomUUID(), "MrRat", ExternalAccountRef("iss", "456"))

        val pendingChallenge = challenger.challenge(opponent)

        assertThat(opponent.accept(pendingChallenge))
            .isEqualTo(AcceptedChallenge(pendingChallenge.id))
    }

    private fun RegisteredPlayer.challenge(opponent: RegisteredPlayer): PendingChallenge =
        challenge(opponent, UsefulString("comment"), UsefulString("tomorrow"), GameMode.Duel)
}