package uk.matvey.corsa

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.cookie
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import uk.matvey.corsa.club.ClubService
import uk.matvey.kit.random.RandomKit.randomName
import uk.matvey.slon.PostgresTestContainer
import uk.matvey.slon.repo.Repo
import java.time.Instant
import java.util.UUID.randomUUID

open class TestSetup {

    companion object {

        private val postgres = PostgresTestContainer()

        lateinit var repo: Repo

        fun dataSource() = postgres.dataSource()

        val algorithm = Algorithm.HMAC256("jwtSecret")

        val athleteId = randomUUID()
        val athleteName = randomName()

        fun testApp(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
            val clubService = ClubService(repo)
            application {
                serverModule(repo, clubService, algorithm)
            }
            val client = createClient {
                defaultRequest {
                    cookie(
                        "token", JWT.create()
                            .withIssuer("corsa")
                            .withSubject(athleteId.toString())
                            .withIssuedAt(Instant.now())
                            .withExpiresAt(Instant.now().plusSeconds(600))
                            .withClaim("name", athleteName)
                            .sign(algorithm)
                    )
                }
            }

            block(client)
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
