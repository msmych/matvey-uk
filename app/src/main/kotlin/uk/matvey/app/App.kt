package uk.matvey.app

fun main() {
//    val drinkiBotConfig = ConfigFactory.load("drinki-bot-dev.conf")
//    val appConfig = ConfigFactory.load("matvey-app-dev.conf")
//    val ds = dataSource(drinkiBotConfig)
//    val drinkiRepos = DrinkiRepos(ds)
//    val accountService = AccountService(drinkiRepos.accountRepo)
//    val drinkService = DrinkService(drinkiRepos.drinkRepo, drinkiRepos.ingredientRepo)
//    startDrinkiBot(drinkiBotConfig, drinkiRepos, accountService, drinkService)
//    System.setProperty("io.ktor.development", "true")
    startServer(true)
}
