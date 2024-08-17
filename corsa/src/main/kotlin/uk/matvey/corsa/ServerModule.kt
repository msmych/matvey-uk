package uk.matvey.corsa

import io.ktor.server.application.Application
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.installFreeMarker

fun Application.serverModule(
    repo: Repo,
) {
    installFreeMarker("templates")
    setupRouting(repo)
}