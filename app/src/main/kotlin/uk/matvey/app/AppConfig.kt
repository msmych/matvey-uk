package uk.matvey.app

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

class AppConfig(
    private val config: Config,
    val env: Env,
) : Config by config {
    
    enum class Env {
        LOCAL,
        PROD,
        ;
    }
    
    companion object {
        fun load(name: String, env: String): AppConfig {
            return AppConfig(
                ConfigFactory.load("$name.$env.conf"),
                Env.valueOf(env.uppercase()),
            )
        }
    }
}
