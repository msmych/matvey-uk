package uk.matvey.begit

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import mu.KotlinLogging
import uk.matvey.begit.athlete.AthleteRepo
import uk.matvey.begit.bot.startBot
import uk.matvey.begit.club.ClubRepo
import uk.matvey.begit.club.ClubService
import uk.matvey.begit.event.EventRepo
import uk.matvey.begit.server.startServer
import uk.matvey.slon.DataSourceKit.hikariDataSource
import uk.matvey.slon.FlywayKit.flywayMigrate
import uk.matvey.slon.repo.Repo
import javax.sql.DataSource

private val log = KotlinLogging.logger("Begit")

fun main() {
    val config = ConfigFactory.load("local.conf")
    val clean = System.getenv("DB_CLEAN")?.toBoolean() ?: false
    val ds = dataSource(config.getConfig("ds"))
    flywayMigrate(
        dataSource = ds,
        schema = "begit",
        clean = clean,
    )

    val repo = Repo(ds)
    val clubRepo = ClubRepo(repo)
    val eventRepo = EventRepo(repo)
    val athleteRepo = AthleteRepo(repo)
    val clubService = ClubService(repo)
    startServer(athleteRepo, clubRepo, eventRepo)
    startBot(config, repo, clubRepo, eventRepo, athleteRepo, clubService)
}

private fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        config.getString("jdbcUrl"),
        config.getString("username"),
        config.getString("password"),
    )
}
