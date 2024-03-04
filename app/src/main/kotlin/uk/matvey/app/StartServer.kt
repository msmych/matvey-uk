package uk.matvey.app

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.app.AppConfig.Env

fun startServer(config: AppConfig, wait: Boolean) {
    System.setProperty("io.ktor.development", (config.env == Env.LOCAL).toString())
    val watchPaths = if (config.env == Env.LOCAL) {
        listOf("resources")
    } else {
        listOf()
    }
    embeddedServer(Netty, port = 8080, watchPaths = watchPaths) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            staticResources("/assets", "/assets")
            get("/healthcheck") {
                call.respond("OK")
            }
            get("/") {
                call.respond(FreeMarkerContent("index.ftl", null))
            }
        }
    }
        .start(wait = wait)
}
