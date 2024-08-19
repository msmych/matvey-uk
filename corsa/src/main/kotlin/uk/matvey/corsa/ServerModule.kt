package uk.matvey.corsa

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.installFreeMarker

fun Application.serverModule(
    repo: Repo,
    clubService: ClubService,
    algorithm: Algorithm,
) {
    installFreeMarker("templates")
    install(Authentication) {
        register(ServerAuth.Provider(algorithm))
    }
    setupRouting(repo, clubService, algorithm)
}
