package uk.matvey.app

fun main(args: Array<String>) {
    val config = AppConfig.load("matvey-app", args.getOrElse(0) { "local" })
//    val ds = dataSource(drinkiBotConfig)
//    val drinkiRepos = DrinkiRepos(ds)
//    val accountService = AccountService(drinkiRepos.accountRepo)
//    val drinkService = DrinkService(drinkiRepos.drinkRepo, drinkiRepos.ingredientRepo)
//    startDrinkiBot(drinkiBotConfig, drinkiRepos, accountService, drinkService)
    startServer(config, true)
}
