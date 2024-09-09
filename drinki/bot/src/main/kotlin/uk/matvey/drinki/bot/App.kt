package uk.matvey.drinki.bot

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uk.matvey.drinki.DrinkiRepos
import uk.matvey.drinki.account.AccountService
import uk.matvey.drinki.drink.DrinkService
import uk.matvey.slon.hikari.HikariKit.hikariDataSource
import javax.sql.DataSource

private val log = KotlinLogging.logger("drinki-bot")

fun main() = runBlocking {
    val config = ConfigFactory.load("drinki-bot-dev.conf")
    val ds = dataSource(config.getConfig("ds"))
    val drinkiRepos = DrinkiRepos(ds)
    val accountService = AccountService(drinkiRepos.accountRepo)
    val drinkService = DrinkService(drinkiRepos.drinkRepo, drinkiRepos.ingredientRepo)
    startDrinkiBot(config, drinkiRepos, accountService, drinkService)
    while (true) {
        delay(1000)
    }
}

private fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        config.getString("jdbcUrl"),
        config.getString("username"),
        config.getString("password"),
    )
}
