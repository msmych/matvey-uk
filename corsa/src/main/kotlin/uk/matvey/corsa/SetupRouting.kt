package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.corsa.club.ClubResource
import uk.matvey.corsa.club.ClubService
import uk.matvey.corsa.event.EventResource
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.queryParam
import uk.matvey.voron.KtorKit.respondFtl

fun Application.setupRouting(
    repo: Repo,
    clubService: ClubService,
    algorithm: Algorithm,
) {
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
        get("/auth") {
            val token = call.queryParam("token")
            try {
                JWT.require(algorithm)
                    .withIssuer("corsa")
                    .build()
                    .verify(token)
            } catch (e: JWTVerificationException) {
                return@get call.respond(Unauthorized)
            }
            call.response.cookies.append("token", token)
            call.respondRedirect("/")
        }
        resources.forEach { resource -> with(resource) { routing() } }
    }
}