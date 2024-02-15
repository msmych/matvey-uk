package uk.matvey.drinki.app.ingredient

import io.ktor.server.application.call
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import uk.matvey.drinki.ingredient.IngredientRepo

fun Route.ingredientRouting(ingredientRepo: IngredientRepo) {
    route("/ingredients") {
        get("/new-ingredient") {
            val ingredients = ingredientRepo.publicIngredients()
            call.respond(FreeMarkerContent("ingredients/new-ingredient.ftl", mapOf("ingredients" to ingredients)))
        }
    }
}
