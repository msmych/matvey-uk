package uk.matvey.corsa

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.slon.repo.Repo

class ServerTest : TestSetup() {

    @Test
    fun `should start server`() = testApplication {
        // given
        application {
            serverModule(Repo(dataSource()))
        }

        // when
        val rs = client.get("/healthcheck")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo("OK")
    }
}