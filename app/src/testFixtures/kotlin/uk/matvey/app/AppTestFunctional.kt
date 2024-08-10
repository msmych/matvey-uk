package uk.matvey.app

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.junit.jupiter.api.BeforeAll

open class AppTestFunctional {
    
    val http = HttpClient(CIO)
    
    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            val config = AppConfig.load("matvey-app", "local")
            startServer(config, false)
        }
    }
}
