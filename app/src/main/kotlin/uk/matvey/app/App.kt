package uk.matvey.app

fun main(args: Array<String>) {
    val env = args.getOrElse(0) { "local" }
    val config = AppConfig.load("matvey", env)
    startServer(config, true)
}
