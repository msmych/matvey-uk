package uk.matvey.corsa

import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import uk.matvey.slon.repo.Repo

private val log = KotlinLogging.logger("Corsa")

fun main() {
    log.info { "Hello, Corsa!" }
    val config = ConfigFactory.load()
    val ds = dataSource(config)
    migrate(ds, clean = false)
    val repo = Repo(ds)
    startServer(repo)
}