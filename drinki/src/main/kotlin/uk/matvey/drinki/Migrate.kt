package uk.matvey.drinki

import kotlinx.coroutines.runBlocking
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.Ingredient.Type.BITTER
import uk.matvey.drinki.ingredient.Ingredient.Type.JUICE
import uk.matvey.drinki.ingredient.Ingredient.Type.OTHER
import uk.matvey.drinki.ingredient.Ingredient.Type.SPIRIT
import uk.matvey.drinki.ingredient.Ingredient.Type.SYRUP
import uk.matvey.drinki.ingredient.Ingredient.Type.TONIC
import uk.matvey.drinki.ingredient.Ingredient.Type.WINE
import uk.matvey.slon.FlywayKit.flywayMigrate

fun migrate(drinkiRepos: DrinkiRepos, clean: Boolean) {
    flywayMigrate(
        dataSource = drinkiRepos.ds,
        schema = "drinki",
        location = "classpath:db/migration/drinki",
        clean = clean,
    )
    migratePublicIngredients(drinkiRepos)
}

private fun migratePublicIngredients(drinkiRepos: DrinkiRepos) = runBlocking {
    val ingredientRepo = drinkiRepos.ingredientRepo
    val publicIngredientsTypes = ingredientRepo.findAllByAccountId(null).map { it.type }
    listOf(
        Ingredient.public(SPIRIT, "Gin"),
        Ingredient.public(SPIRIT, "Rum"),
        Ingredient.public(SPIRIT, "Tequila"),

        Ingredient.public(BITTER, "Aperol"),
        Ingredient.public(BITTER, "Angostura"),
        Ingredient.public(BITTER, "Triple sec"),

        Ingredient.public(WINE, "Prosecco"),
        Ingredient.public(WINE, "Red wine"),
        Ingredient.public(WINE, "White port"),

        Ingredient.public(JUICE, "Lime juice"),
        Ingredient.public(JUICE, "Lemon juice"),

        Ingredient.public(SYRUP, "Simple syrup"),

        Ingredient.public(TONIC, "Soda water"),
        Ingredient.public(TONIC, "Ginger ale"),
        Ingredient.public(TONIC, "Cola"),

        Ingredient.public(OTHER, "Sugar"),
        Ingredient.public(OTHER, "Salt"),
        Ingredient.public(OTHER, "Mint"),
    )
        .filter { it.type !in publicIngredientsTypes }
        .forEach { ingredient ->
            ingredientRepo.add(ingredient)
        }
}
