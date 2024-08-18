package uk.matvey.corsa

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.installFreeMarker

fun Application.serverModule(
    repo: Repo,
    clubService: ClubService,
    algorithm: Algorithm,
) {
    installFreeMarker("templates")
    setupRouting(repo, clubService, algorithm)
}