package uk.matvey.app

import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import uk.matvey.app.config.AppConfig.ServerConfig
import uk.matvey.falafel.FalafelAuth
import uk.matvey.falafel.falafelServerModule
import uk.matvey.slon.repo.Repo
import uk.matvey.tmdb.TmdbClient
import java.io.File
import java.io.FileInputStream
import java.security.KeyStore

fun startMatveyServer(
    serverConfig: ServerConfig,
    profile: Profile,
    falafelAuth: FalafelAuth,
    auth: MatveyAuth,
    repo: Repo,
    tmdbClient: TmdbClient,
) {
    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            if (profile.isProd()) {
                val jksPass = serverConfig.jksPass().toCharArray()
                val keyStoreFile = File("/certs/keystore.jks")
                sslConnector(
                    keyStore = KeyStore.getInstance("JKS").apply {
                        load(FileInputStream(keyStoreFile), jksPass)
                    },
                    keyAlias = "matvey-p12",
                    privateKeyPassword = { jksPass },
                    keyStorePassword = { jksPass },
                ) {
                    port = serverConfig.port()
                    keyStorePath = keyStoreFile
                    module {
                        matveyServerModule(serverConfig, auth, repo)
                    }
                }
            } else {
                connector {
                    port = serverConfig.port()
                    watchPaths = when (profile) {
                        Profile.LOCAL,
                        Profile.TEST -> listOf("resources", "classes")
                        else -> listOf()
                    }
                    module {
                        matveyServerModule(serverConfig, auth, repo)
                        falafelServerModule(falafelAuth, repo, tmdbClient)
                    }
                }
            }
        },
    )
        .start(wait = true)
}
