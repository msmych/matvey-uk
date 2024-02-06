package uk.matvey.postal

import com.typesafe.config.Config
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

fun dataSource(config: Config): DataSource {
    val hikariConfig = HikariConfig()
    hikariConfig.jdbcUrl = config.getString("ds.jdbcUrl")
    hikariConfig.username = config.getString("ds.username")
    hikariConfig.password = config.getString("ds.password")
    hikariConfig.driverClassName = "org.postgresql.Driver"
    val ds = HikariDataSource(hikariConfig)
    return ds
}
