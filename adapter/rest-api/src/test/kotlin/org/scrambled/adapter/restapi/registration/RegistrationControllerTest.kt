package org.scrambled.adapter.restapi.registration

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.scrambled.adapter.restapi.RestApiTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
@SpringJUnitWebConfig
@ContextConfiguration(classes = [RestApiTestConfig::class])
internal class RegistrationControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    internal fun `an authenticated user can register themselves`() {
        val result = mvc.perform {
            post("/api/register")
                .content("""{ "name": "Snarf" }""")
                .contentType(MediaType.APPLICATION_JSON)
                .buildRequest(it)
        }.andExpect(status().isOk())
            .andReturn()
            .response.contentAsString

        Assertions.assertThat(result).isEqualTo("""{"name":"Snarf"}""")
    }
}