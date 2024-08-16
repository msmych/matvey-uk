package uk.matvey.corsa

import io.ktor.server.application.Application
import uk.matvey.slon.repo.Repo
import uk.matvey.voron.KtorKit.installFtl

fun Application.serverModule(
    repo: Repo,
) {
    installFtl("templates")
    setupRouting(repo)
}