package uk.matvey.drinki.app

import com.typesafe.config.ConfigFactory
import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.freemarker.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
            get("/drinki") {
                call.respond(FreeMarkerContent("drinki.ftl", null))
            }
            route("/drinks") {
                post {
                    val drink = Drink.new(randomUUID())
                    drinkRepo.add(drink)
                    call.respond(FreeMarkerContent("drink-edit.ftl", mapOf("drink" to drink)))
                }
            }
        }
    }
        .start(wait = true)
}
