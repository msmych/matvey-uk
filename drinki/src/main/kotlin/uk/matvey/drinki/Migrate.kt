package uk.matvey.drinki

import org.flywaydb.core.Flyway
import uk.matvey.drinki.ingredient.Ingredient
import uk.matvey.drinki.ingredient.Ingredient.Type.*

fun migrate(repos: Repos, clean: Boolean) {
    val flyway = Flyway.configure()
        .dataSource(repos.ds)
        .schemas("drinki")
        .locations("classpath:db/migration")
        .defaultSchema("drinki")
        .createSchemas(true)
        .cleanDisabled(!clean)
        .load()
    if (clean) {
        flyway.clean()
    }
    flyway
        .migrate()
    migratePublicIngredients(repos)
}

private fun migratePublicIngredients(repos: Repos) {
    val ingredientRepo = repos.ingredientRepo
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