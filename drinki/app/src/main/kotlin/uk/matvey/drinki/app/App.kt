package uk.matvey.drinki.app

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
import javax.sql.DataSource

fun main() {
    val config = ConfigFactory.load("drinki-app.conf")
    val ds = dataSource(config)
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
    val hikariConfig = HikariConfig()
    hikariConfig.jdbcUrl = config.getString("ds.jdbcUrl")
    hikariConfig.username = config.getString("ds.username")
    hikariConfig.password = config.getString("ds.password")
    hikariConfig.driverClassName = "org.postgresql.Driver"
    val ds = HikariDataSource(hikariConfig)
    return ds
}
