package uk.matvey.corsa

import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.corsa.ServerAuth.AthletePrincipal
import uk.matvey.corsa.club.ClubResource
import uk.matvey.corsa.club.ClubService
import uk.matvey.corsa.event.EventResource
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

fun Application.setupRouting(
    repo: Repo,
    clubService: ClubService,
    auth: AuthJwt,
) {
    val auth = ServerAuth(auth)
    val resources = listOf(
        ClubResource(repo, clubService),
        EventResource(repo, clubService),
    )
    routing {
        staticResources("/assets", "/assets")
        get("/healthcheck") {
            call.respondText("OK")
        }
        get {
            call.respondFtl("index")
        }
        authenticate("jwt") {
            get("/me") {
                val athlete = call.principal<AthletePrincipal>() ?: return@get call.respond(Unauthorized)
                call.respondFtl("me", "name" to athlete.name)
            }
        }
        get("/login") {
            call.respondFtl("login")
        }
        with(auth) { authRouting() }
        resources.forEach { resource ->
            with(resource) {
                authenticate("jwt") {
                    routing()
                }
            }
        }
    }
}
