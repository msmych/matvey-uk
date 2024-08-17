package uk.matvey.corsa

import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import uk.matvey.corsa.club.ClubService
import uk.matvey.slon.PostgresTestContainer
import uk.matvey.slon.repo.Repo

open class TestSetup {

    companion object {

        private val postgres = PostgresTestContainer()

        lateinit var repo: Repo

        fun dataSource() = postgres.dataSource()

        fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
            val clubService = ClubService(repo)
            application {
                serverModule(repo, clubService)
            }

            block()
        }

        @BeforeAll
        @JvmStatic
        fun globalSetup() {
            postgres.start()
            repo = Repo(dataSource())
            migrate(dataSource(), clean = true)
        }

        @AfterAll
        @JvmStatic
        fun globalTeardown() {
            postgres.stop()
        }
    }
}
