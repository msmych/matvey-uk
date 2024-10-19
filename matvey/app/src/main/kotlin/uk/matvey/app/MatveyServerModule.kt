package uk.matvey.app

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.date.GMTDate
import uk.matvey.app.account.AccountPrincipal
import uk.matvey.app.account.AccountResource
import uk.matvey.app.config.AppConfig.ServerConfig
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.installFreeMarker
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.matveyServerModule(
    serverConfig: ServerConfig,
    auth: MatveyAuth,
    repo: Repo
) {
    installFreeMarker("templates")
    install(Authentication) {
        register(auth)
    }
    install(StatusPages) {
        exception<AuthException> { call, cause ->
            call.respond(Unauthorized)
        }
    }
    val resources = listOf(
        AccountResource(auth, repo),
    )
    routing {
        staticResources("/assets", "/assets")
        get("/health") {
            call.respondText("OK")
        }
        get("/login") {
            call.respondFtl("login", "assets" to serverConfig.assets())
        }
        authenticate("jwt") {
            get("/me") {
                val principal = call.principal<AccountPrincipal>()
                call.respondFtl("me", "name" to principal?.name)
            }
            get("/logout") {
                call.response.cookies.append("token", "", expires = GMTDate.START)
                call.respondRedirect("/")
            }
            resources.forEach { with(it) { routing() } }
            get("/") {
                val principal = call.principal<AccountPrincipal>()
                call.respondFtl("index", "account" to principal, "assets" to serverConfig.assets())
            }
        }
        with(auth) { authRouting() }
    }
}
