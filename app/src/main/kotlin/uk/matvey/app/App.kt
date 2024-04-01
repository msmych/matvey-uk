package uk.matvey.app

import uk.matvey.migraine.frobot.startFrobot

fun main(args: Array<String>) {
    val env = args.getOrElse(0) { "local" }
    val config = AppConfig.load("matvey", env)
    startBot(config)
    startFrobot(AppConfig.load("frobot", env))
    startServer(config, true)
}
