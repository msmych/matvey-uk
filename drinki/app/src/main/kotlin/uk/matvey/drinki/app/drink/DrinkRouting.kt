package uk.matvey.drinki.app.drink

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.drinkRouting() {
    route("/drinks") {
        post {
            call.respond(FreeMarkerContent("drinks/new-drink.ftl", null))
        }
    }
}
