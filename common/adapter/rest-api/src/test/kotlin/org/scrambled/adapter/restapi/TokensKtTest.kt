package org.scrambled.adapter.restapi

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.scrambled.adapter.restapi.extensions.asJwt
import org.scrambled.adapter.restapi.extensions.removeBearer

class TokensKtTest {

    @Test
    fun `removeBearer - removes the passed prefix from the given string`() {
        val actual = "Bearer FreDumazyAKAMyConscience".removeBearer()

        assertThat(actual).isEqualTo("FreDumazyAKAMyConscience")
    }

    @Test
    fun `removeBearer - returns the string if no prefix was found`() {
        val actual = "FreDumazyAKAMyConscience".removeBearer()

        assertThat(actual).isEqualTo("FreDumazyAKAMyConscience")
    }

    @Test
    fun `removeBearer - with prefix not at the beginning of the string should just return the string as is`() {
        val actual = "FreDumazyAKAMyConscienceBearer ".removeBearer()

        assertThat(actual).isEqualTo("FreDumazyAKAMyConscienceBearer ")
    }

    @Test
    fun `asJwt - can transform an encoded bearer token into a Jwt`() {
        val dummyEncodedJwt = JwtInfo("epic", "schlep").asDummyEncodedJwt()

        val actual = "$dummyEncodedJwt".asJwt()

        assertThat(actual.tokenValue).doesNotContain("\n")
    }
}

