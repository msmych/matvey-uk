package uk.matvey.drinki

import uk.matvey.drinki.account.AccountRepo
import uk.matvey.drinki.drink.DrinkRepo
import uk.matvey.drinki.ingredient.IngredientRepo
import uk.matvey.slon.Repo
import javax.sql.DataSource

class DrinkiRepos(
    val ds: DataSource,
) {

    val repo = Repo(ds)

    val accountRepo = AccountRepo(repo)
    val ingredientRepo = IngredientRepo(repo)
    val drinkRepo = DrinkRepo(repo)
}
