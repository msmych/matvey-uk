package uk.matvey.corsa

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.corsa.club.ClubResource
import uk.matvey.corsa.club.ClubService
import uk.matvey.corsa.event.EventResource
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.respondFtl

fun Application.setupRouting(
    repo: Repo,
    clubService: ClubService,
    algorithm: Algorithm,
) {
    val auth = ServerAuth(algorithm)
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
        with(auth) { authRouting() }
        resources.forEach { resource -> with(resource) { routing() } }
    }
}
