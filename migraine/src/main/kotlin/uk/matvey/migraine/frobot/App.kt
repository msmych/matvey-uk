package uk.matvey.migraine.frobot

import com.typesafe.config.ConfigFactory

fun main() {
    val config = ConfigFactory.load("frobot-local.conf")
    startFrobot(config)
    while (true) {
        Thread.sleep(1000)
    }
}
