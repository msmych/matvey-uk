package uk.matvey.corsa

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo
import uk.matvey.utka.jwt.AuthJwt
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.installFreeMarker

fun Application.serverModule(
    repo: Repo,
    clubService: ClubService,
    auth: AuthJwt,
) {
    installFreeMarker("templates")
    install(Authentication) {
        register(ServerAuth.Provider(auth))
    }
    setupRouting(repo, clubService, auth)
}
