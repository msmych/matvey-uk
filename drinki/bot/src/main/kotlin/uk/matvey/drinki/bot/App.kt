package uk.matvey.drinki.bot

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.drinki.DrinkiRepos
import uk.matvey.drinki.account.AccountService
import uk.matvey.postal.dataSource

private val log = KotlinLogging.logger("drinki-bot")

fun main() = runBlocking {
    val config = ConfigFactory.load("drinki-bot.conf")
    val ds = dataSource(config)
    val repos = DrinkiRepos(ds)
    migrate(repos, System.getenv("CLEAN_DB") != null)
    val accountService = AccountService(repos.accountRepo)
    startBot(
        config,
        repos.accountRepo,
        accountService,
        repos.drinkRepo,
        repos.ingredientRepo,
    )
    while (true) {
        delay(1000)
    }
}
