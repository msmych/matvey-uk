package uk.matvey.app

import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.runBlocking
import uk.matvey.slon.FlywayKit.flywayMigrate
import uk.matvey.slon.HikariKit.hikariDataSource
import uk.matvey.slon.repo.Repo

fun main(args: Array<String>) {
    val profile = args[0]
    val config = ConfigFactory.load("matvey.$profile.conf")
        .withFallback(ConfigFactory.load("matvey.conf"))
    val dbConfig = config.getConfig("db")
    val ds = hikariDataSource(
        jdbcUrl = dbConfig.getString("jdbcUrl"),
        username = dbConfig.getString("username"),
        password = dbConfig.getString("password"),
    )
    val tgConfig = config.getConfig("tg")
    flywayMigrate(
        dataSource = ds,
        clean = dbConfig.getBoolean("clean"),
    ) {
        placeholders(
            mapOf(
                "tgAdminId" to tgConfig.getLong("adminId").toString(),
            )
        )
    }
    val repo = Repo(ds)
    runBlocking {
        MatveyBot(tgConfig, repo).start().join()
    }
}
