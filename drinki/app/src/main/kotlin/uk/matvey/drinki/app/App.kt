package uk.matvey.drinki.app

import com.typesafe.config.Config
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
import io.ktor.server.routing.routing
import uk.matvey.drinki.DrinkiRepos
import uk.matvey.drinki.app.drink.drinkRouting
import uk.matvey.drinki.app.ingredient.ingredientRouting
import uk.matvey.drinki.migrate
import uk.matvey.slon.hikari.HikariKit.hikariDataSource
import javax.sql.DataSource

fun main() {
    val config = ConfigFactory.load("drinki-app.conf")
    val ds = dataSource(config.getConfig("ds"))
    val drinkiRepos = DrinkiRepos(ds)
    val drinkRepo = drinkiRepos.drinkRepo
    migrate(drinkiRepos, false)
    embeddedServer(Netty, 8080) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }
        routing {
            staticResources("/assets", "/assets")
            get("/") {
                call.respond(FreeMarkerContent("drinki.ftl", null))
            }
            drinkRouting()
            ingredientRouting(drinkiRepos.ingredientRepo)
        }
    }
        .start(wait = true)
}

private fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        config.getString("jdbcUrl"),
        config.getString("username"),
        config.getString("password"),
    )
}
