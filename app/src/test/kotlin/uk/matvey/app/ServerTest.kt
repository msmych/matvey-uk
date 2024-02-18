package uk.matvey.app

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ServerTest : AppTestFunctional() {
    
    @Test
    fun `should respond healthcheck OK`() = runBlocking<Unit> {
        val rs = http.get("http://localhost:8080/healthcheck")
        
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo("OK")
    }
}
