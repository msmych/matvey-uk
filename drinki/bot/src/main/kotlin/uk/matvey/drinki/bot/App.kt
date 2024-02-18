package uk.matvey.drinki.bot

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.drinki.DrinkiRepos
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.drinki.migrate
import uk.matvey.postal.dataSource

private val log = KotlinLogging.logger("drinki-bot")

fun main() = runBlocking {
    val config = ConfigFactory.load("drinki-bot.conf")
    val ds = dataSource(config)
    val drinkiRepos = DrinkiRepos(ds)
    migrate(drinkiRepos, System.getenv("CLEAN_DB") != null)
    val accountService = AccountService(drinkiRepos.accountRepo)
    val drinkService = DrinkService(drinkiRepos.drinkRepo, drinkiRepos.ingredientRepo)
    startBot(
        config,
        drinkiRepos.accountRepo,
        accountService,
        drinkiRepos.drinkRepo,
        drinkiRepos.ingredientRepo,
        drinkService,
    )
    while (true) {
        delay(1000)
    }
}
