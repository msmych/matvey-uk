package uk.matvey.corsa.club

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.corsa.TestSetup
import uk.matvey.corsa.club.ClubTestData.aClub
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertInto

class ClubResourceTest : TestSetup() {

    @Test
    fun `should return clubs`() = testApplication {
        // given
        val repo = Repo(dataSource())
        application {
            serverModule()
        }
        val club1 = aClub()
        val club2 = aClub()
        repo.insertInto("clubs") {
            values(
                "name" to text(club1.name),
                "updated_at" to now()
            )
        }
        repo.insertInto("clubs") {
            values(
                "name" to text(club2.name),
                "updated_at" to now()
            )
        }

        // when
        val rs = client.get("/clubs")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(">${club1.name}<")
            .contains(">${club2.name}<")
    }

    @Test
    fun `should return new club form`() = testApplication {
        // given
        application {
            serverModule()
        }

        // when
        val rs = client.get("/clubs/new-club-form")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains("<h3>New club</h3>")
    }
}