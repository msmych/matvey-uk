package uk.matvey.app

import com.typesafe.config.ConfigFactory
import uk.matvey.drinki.DrinkiRepos
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.bot.startBot
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.postal.dataSource

fun main() {
    System.setProperty("io.ktor.development", "true")
    val drinkiBotConfig = ConfigFactory.load("drinki-bot.conf")
    val appConfig = ConfigFactory.load("matvey-app.conf")
    val ds = dataSource(appConfig)
    val drinkiRepos = DrinkiRepos(ds)
    val accountService = AccountService(drinkiRepos.accountRepo)
    val drinkService = DrinkService(drinkiRepos.drinkRepo, drinkiRepos.ingredientRepo)
    startBot(
        drinkiBotConfig,
        drinkiRepos.accountRepo,
        accountService,
        drinkiRepos.drinkRepo,
        drinkiRepos.ingredientRepo,
        drinkService,
    )
    startServer(true)
}
