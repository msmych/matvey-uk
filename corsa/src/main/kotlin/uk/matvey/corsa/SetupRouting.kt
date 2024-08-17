package uk.matvey.corsa

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.corsa.club.ClubResource
import uk.matvey.corsa.event.EventResource
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.respondFtl

fun Application.setupRouting(
    repo: Repo,
) {
    val resources = listOf(
        ClubResource(repo),
        EventResource(),
    )
    routing {
        staticResources("/assets", "/assets")
        get("/healthcheck") {
            call.respondText("OK")
        }
        get {
            call.respondFtl("index")
        }
        resources.forEach { resource -> with(resource) { routing() } }
    }
}