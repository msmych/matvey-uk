package uk.matvey.corsa

import io.ktor.server.application.Application
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import uk.matvey.slon.PostgresTestContainer
import uk.matvey.slon.repo.Repo

open class TestSetup {

    companion object {

        private val postgres = PostgresTestContainer()

        fun dataSource() = postgres.dataSource()

        fun repo() = Repo(dataSource())

        fun Application.serverModule() = serverModule(repo())

        @BeforeAll
        @JvmStatic
        fun globalSetup() {
            postgres.start()
            migrate(dataSource(), clean = true)
        }

        @AfterAll
        @JvmStatic
        fun globalTeardown() {
            postgres.stop()
        }
    }
}
