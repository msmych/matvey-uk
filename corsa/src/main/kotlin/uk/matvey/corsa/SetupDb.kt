package uk.matvey.corsa

import com.typesafe.config.Config
import uk.matvey.slon.DataSourceKit.hikariDataSource
import uk.matvey.slon.FlywayKit.flywayMigrate
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
        dataSource,
        schema = "public",
        location = "classpath:db/migration",
        createSchema = false,
        clean = clean,
    )
}