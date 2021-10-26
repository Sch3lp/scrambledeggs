package org.scrambled.core.impl.players

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.scrambled.core.impl.challenges.PendingChallenge
import org.scrambled.core.impl.challenges.ChallengeRepository
import org.scrambled.domain.core.api.UsefulString
import org.scrambled.domain.core.api.challenges.ChallengePlayer
import org.scrambled.domain.core.api.challenges.GameMode
import org.scrambled.domain.core.api.registration.ExternalAccountRef
import org.scrambled.infra.retry.RetryException
import java.util.*

@ExtendWith(MockitoExtension::class)
class ChallengePlayerHandlerTest {
    @Mock
    private lateinit var playerRepo: RegisteredPlayerRepository
    @Mock
    private lateinit var challengeRepo: ChallengeRepository

    @Test
    fun `challenging a player is retried until the challengeId is unique for at most 5 times`() {
        val handler = ChallengePlayerHandler(playerRepo, challengeRepo)

        val sch3lp = player("Sch3lp")
        val coredusk = player("CoreDusk")

        Mockito.`when`(challengeRepo.exists(any()))
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        val (challengeId, _) = handler.handle(challengePlayerCmd(sch3lp, coredusk))

        val challengeCaptor = argumentCaptor<PendingChallenge>()
        Mockito.verify(challengeRepo).save(challengeCaptor.capture())

        assertThat(challengeCaptor.firstValue.challengeId).isEqualTo(challengeId)
    }

    @Test
    fun `when no unique challengeId could be generated, an error is thrown`() {
        val handler = ChallengePlayerHandler(playerRepo, challengeRepo)

        val sch3lp = player("Sch3lp")
        val coredusk = player("CoreDusk")

        Mockito.`when`(challengeRepo.exists(any()))
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false)

        assertThatExceptionOfType(RetryException::class.java)
            .isThrownBy { handler.handle(challengePlayerCmd(sch3lp, coredusk)) }
            .withMessage("Couldn't create a pending challenge with unique id.")
    }

    private fun challengePlayerCmd(
        sch3lp: RegisteredPlayer,
        coredusk: RegisteredPlayer
    ) = ChallengePlayer(
        sch3lp.id,
        coredusk.id,
        UsefulString("comment"),
        UsefulString("next week"),
        GameMode.CTF
    )

    private fun player(
        nickName: String
    ) = RegisteredPlayer(
        UUID.randomUUID(),
        nickName,
        ExternalAccountRef("iss", nickName)
    ).also { p -> Mockito.`when`(playerRepo.getById(p.id)).thenReturn(p) }
}