package uk.matvey.app

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.app.AppConfig.Env
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun startServer(config: AppConfig, wait: Boolean) {
    val environment = applicationEngineEnvironment {
        connector {
            port = 8080
        }
        watchPaths = if (config.env == Env.LOCAL) {
            listOf("resources")
        } else {
            listOf()
        }
        developmentMode = config.env == Env.LOCAL
        if (config.env == Env.PROD) {
            val keyStoreFile = File("/certs/keystore.jks")
            val p12Pass = System.getenv("P12_PASS")
            val jksPass = System.getenv("JKS_PASS")
            sslConnector(
                keyStore = KeyStore.getInstance("JKS").apply {
                    load(FileInputStream(keyStoreFile), jksPass.toCharArray())
                },
                keyAlias = "matveyAppCert",
                privateKeyPassword = { p12Pass.toCharArray() },
                keyStorePassword = { jksPass.toCharArray() }) {
                port = 8443
                keyStorePath = keyStoreFile
            }
        }
        module {
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
    }
    embeddedServer(Netty, environment)
        .start(wait = wait)
}
