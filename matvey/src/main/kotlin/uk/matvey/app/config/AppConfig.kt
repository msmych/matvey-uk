package uk.matvey.app.config

import com.typesafe.config.Config
import uk.matvey.slon.HikariKit.hikariDataSource

class AppConfig(private val config: Config) : Config by config {

    class ServerConfig(private val config: Config) : Config by config {

        fun url() = getString("host") + ":" + getInt("port")
    }

    class DbConfig(private val config: Config) : Config by config {

        fun ds() = hikariDataSource(
            jdbcUrl = getString("jdbcUrl"),
            username = getString("username"),
            password = getString("password"),
        )

        fun clean() = getBoolean("clean")
    }

    class TgConfig(private val config: Config) : Config by config {

        fun adminId() = getLong("adminId")
    }

    fun jwtSecret() = getString("jwtSecret")

    fun server() = ServerConfig(getConfig("server"))

    fun db() = DbConfig(getConfig("db"))

    fun tg() = TgConfig(getConfig("tg"))
}
