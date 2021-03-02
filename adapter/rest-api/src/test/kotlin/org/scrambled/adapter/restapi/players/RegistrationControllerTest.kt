package org.scrambled.adapter.restapi.players

import org.hamcrest.CoreMatchers.containsString
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.scrambled.adapter.restapi.RestApiTestConfig
import org.scrambled.domain.core.api.players.RegisteredPlayerRepresentation
import org.scrambled.domain.core.api.registration.RegisterPlayer
import org.scrambled.infra.cqrs.CommandExecutor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest
@SpringJUnitWebConfig
@ContextConfiguration(classes = [RestApiTestConfig::class])
internal class RegistrationControllerTest {

    @Autowired
    lateinit var commandExecutor: CommandExecutor

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    internal fun `an authenticated user can register themselves`() {
        val registeredPlayerId = UUID.randomUUID()
        Mockito.`when`(commandExecutor.execute(RegisterPlayer(nickname = "Snarf")))
            .thenReturn(RegisteredPlayerRepresentation(registeredPlayerId, "Snarf"))

        mvc.perform {
            post("/api/register")
                .content("""{ "nickname": "Snarf" }""")
                .contentType(MediaType.APPLICATION_JSON)
                .buildRequest(it)
        }
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", containsString("/api/player/$registeredPlayerId")))
    }
}