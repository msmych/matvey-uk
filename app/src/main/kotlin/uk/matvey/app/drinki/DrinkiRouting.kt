package uk.matvey.app.drinki

import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.matvey.drinki.drink.Drink
import uk.matvey.drinki.drink.DrinkRepo
import java.util.UUID.randomUUID

fun Route.drinkiRouting(drinkRepo: DrinkRepo) {
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