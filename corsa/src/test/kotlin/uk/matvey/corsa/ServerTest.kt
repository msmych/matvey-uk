package uk.matvey.corsa

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServerTest : TestSetup() {

    @Test
    fun `should start server`() = testApp {
        // when
        val rs = client.get("/healthcheck")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo("OK")
    }
}