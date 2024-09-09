package uk.matvey.corsa

import com.typesafe.config.Config
import uk.matvey.slon.flyway.FlywayKit.flywayMigrate
import uk.matvey.slon.hikari.HikariKit.hikariDataSource
import javax.sql.DataSource

fun dataSource(config: Config): DataSource {
    return hikariDataSource(
        jdbcUrl = config.getString("jdbcUrl"),
        username = config.getString("username"),
        password = config.getString("password")
    )
}

fun migrate(dataSource: DataSource, clean: Boolean = false) {
    flywayMigrate(
        dataSource = dataSource,
        clean = clean,
    )
}
