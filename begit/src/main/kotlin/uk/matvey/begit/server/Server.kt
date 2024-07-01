package uk.matvey.begit.server

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.event.EventRepo
import uk.matvey.begit.event.eventRouting

fun startServer(
    athleteRepo: AthleteRepo,
    clubRepo: ClubRepo,
    eventRepo: EventRepo,
) {
    embeddedServer(
        Netty, port = 8080, watchPaths = listOf("resources", "classes")
    ) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            staticResources("/assets", "/assets")
            get("/healthcheck") {
                call.respondText("OK")
            }
            begitRouting(athleteRepo, clubRepo)
            eventRouting(eventRepo)
        }
    }.start(wait = false)
}