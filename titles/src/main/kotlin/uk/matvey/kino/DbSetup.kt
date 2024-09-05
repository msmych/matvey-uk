package uk.matvey.kino

import com.typesafe.config.Config
import uk.matvey.slon.FlywayKit.flywayMigrate
import uk.matvey.slon.HikariKit.hikariDataSource
import uk.matvey.slon.repo.Repo

class DbSetup(
    private val dataSourceConfig: Config,
) {

    private val dataSource = hikariDataSource(
        jdbcUrl = dataSourceConfig.getString("jdbcUrl"),
        username = dataSourceConfig.getString("username"),
        password = dataSourceConfig.getString("password"),
    )

    private val repo = Repo(dataSource)

    fun migrate() {
        flywayMigrate(
            dataSource = dataSource,
            schema = "titles",
            location = "classpath:db/migration/titles",
            clean = dataSourceConfig.getBoolean("clean"),
        ) {
            createSchemas(true)
        }
    }
}
