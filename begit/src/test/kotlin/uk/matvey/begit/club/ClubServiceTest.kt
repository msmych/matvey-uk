package uk.matvey.begit.club

import com.zaxxer.hikari.HikariDataSource
import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import uk.matvey.slon.Repo

class ClubServiceTest {

    private val postgres = PostgreSQLContainer("postgres")

    @Test
    fun `should add club`() {
        // given
        postgres.start()
        val dataSource = HikariDataSource().apply {
            jdbcUrl = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
        }

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .schemas("begit")
            .defaultSchema("begit")
            .createSchemas(true)
            .cleanDisabled(true)
            .load()
        flyway.migrate()

        // when
        val (club, count) = ClubService(Repo(dataSource)).ensureClub("club1", 1L)

        // then
        assertThat(club.name).isEqualTo("club1")
        assertThat(club.refs.tgId).isEqualTo(1L)
        assertThat(count).isEqualTo(0)
    }
}