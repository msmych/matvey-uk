package uk.matvey.corsa

import mu.KotlinLogging

private val log = KotlinLogging.logger("Corsa")

fun main() {
    log.info { "Hello, Corsa!" }
    startServer()
}