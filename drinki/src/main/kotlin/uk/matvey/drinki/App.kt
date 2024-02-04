package uk.matvey.drinki

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.bot.startBot

private val log = KotlinLogging.logger("drinki")

fun main() = runBlocking {
    val config = ConfigFactory.load()
    val ds = dataSource(config)
    val repos = Repos(ds)
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
