package uk.matvey.begit

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import uk.matvey.begit.bot.startBot
import uk.matvey.begit.club.ClubService
import uk.matvey.slon.Repo
import javax.sql.DataSource

private val log = KotlinLogging.logger("Begit")

fun main() {
    val config = ConfigFactory.load("local.conf")
    val ds = dataSource(config.getConfig("ds"))
    val flyway = Flyway.configure()
        .dataSource(ds)
        .schemas("begit")
        .defaultSchema("begit")
        .createSchemas(true)
        .cleanDisabled(true)
        .load()
    log.info { flyway.migrate() }

    val repo = Repo(ds)
    val clubService = ClubService(repo)
    startBot(config, clubService)
}

private fun dataSource(config: Config): DataSource {
    return HikariDataSource().apply {
        jdbcUrl = config.getString("jdbcUrl")
        username = config.getString("username")
        password = config.getString("password")
        driverClassName = config.getString("driverClassName")
    }
}

