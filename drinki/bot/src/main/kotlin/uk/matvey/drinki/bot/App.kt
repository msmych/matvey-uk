package uk.matvey.drinki.bot

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.drinki.Repos
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.postal.dataSource

private val log = KotlinLogging.logger("drinki-bot")

fun main() = runBlocking {
    val config = ConfigFactory.load("drinki-bot.conf")
    val ds = dataSource(config)
    val repos = Repos(ds)
    migrate(repos, System.getenv("CLEAN_DB") != null)
    val accountService = AccountService(repos.accountRepo)
    val drinkService = DrinkService(repos.drinkRepo, repos.ingredientRepo)
    startBot(
        config,
        repos.accountRepo,
        accountService,
        repos.drinkRepo,
        repos.ingredientRepo,
        drinkService,
    )
    while (true) {
        delay(1000)
    }
}
