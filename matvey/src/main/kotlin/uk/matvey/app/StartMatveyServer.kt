package uk.matvey.app

import com.typesafe.config.Config
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import uk.matvey.falafel.falafelServerModule
import uk.matvey.slon.repo.Repo
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun startMatveyServer(
    serverConfig: Config,
    profile: Profile,
    auth: MatveyAuth,
    repo: Repo,
) {
    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            if (profile.isProd()) {
                val jksPass = serverConfig.getString("jskPass").toCharArray()
                val keyStoreFile = File("/certs/keystore.jks")
                sslConnector(
                    keyStore = KeyStore.getInstance("JKS").apply {
                        load(FileInputStream(keyStoreFile), jksPass)
                    },
                    keyAlias = "matvey-p12",
                    privateKeyPassword = { jksPass },
                    keyStorePassword = { jksPass },
                ) {
                    port = serverConfig.getInt("port")
                    keyStorePath = keyStoreFile
                    module {
                        matveyServerModule(auth, repo)
                        falafelServerModule()
                    }
                }
            } else {
                connector {
                    port = serverConfig.getInt("port")
                    watchPaths = when (profile) {
                        Profile.LOCAL,
                        Profile.TEST -> listOf("resources", "classes")
                        else -> listOf()
                    }
                    module {
                        matveyServerModule(auth, repo)
                        falafelServerModule()
                    }
                }
            }
        },
    )
        .start(wait = true)
}
