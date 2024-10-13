package uk.matvey.app.config

import com.typesafe.config.Config
import uk.matvey.app.Profile
import uk.matvey.slon.hikari.HikariKit.hikariDataSource

class AppConfig(private val config: Config) : Config by config {

    class ServerConfig(private val config: Config) : Config by config {

        fun host() = getString("host")

        fun port() = getInt("port")

        fun url(profile: Profile) = if (profile !in setOf(Profile.TEST, Profile.LOCAL)) {
            host()
        } else {
            host() + ":" + port()
        }

        fun jksPass() = getString("jksPass")

        fun assets() = getString("assets")
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

        fun botToken() = getString("botToken")

        fun longPollingSeconds() = getInt("longPollingSeconds")

        fun adminId() = getLong("adminId")

        fun adminGroupId() = getLong("adminGroupId")
    }

    class TmdbConfig(private val config: Config) : Config by config {

        fun token() = getString("token")
    }

    fun jwtSecret() = getString("jwtSecret")

    fun server() = ServerConfig(getConfig("server"))

    fun db() = DbConfig(getConfig("db"))

    fun tg() = TgConfig(getConfig("tg"))

    fun tmdb() = TmdbConfig(getConfig("tmdb"))
}
