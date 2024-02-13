package uk.matvey.drinki.app

import com.typesafe.config.ConfigFactory
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
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import uk.matvey.drinki.Repos
import uk.matvey.drinki.drink.Drink
import uk.matvey.postal.dataSource
import java.util.UUID.randomUUID

fun main() {
    val config = ConfigFactory.load("drinki-app.conf")
    val ds = dataSource(config)
    val repos = Repos(ds)
    val drinkRepo = repos.drinkRepo
    embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            staticResources("/", "public")
            get("/drinki") {
                call.respond(FreeMarkerContent("drinki.ftl", null))
            }
            route("/drinks") {
                post {
                    val drink = Drink.new(randomUUID())
                    drinkRepo.add(drink)
                    call.respond(FreeMarkerContent("drink-edit.ftl", mapOf("drink" to drink)))
                }
                get("/new-ingredient") {
                    call.respond(FreeMarkerContent("drinks/new-ingredient.ftl", null))
                }
            }
        }
    }
        .start(wait = true)
}
