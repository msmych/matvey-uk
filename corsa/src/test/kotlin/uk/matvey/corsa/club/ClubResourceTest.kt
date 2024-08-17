package uk.matvey.corsa.club

import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.corsa.TestSetup
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.corsa.club.ClubTestData.aClub
import uk.matvey.kit.random.RandomKit.randomAlphabetic
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertInto
import uk.matvey.slon.repo.RepoKit.queryOne

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

    @Test
    fun `should add club`() = testApplication {
        // given
        application {
            serverModule()
        }
        val name = randomAlphabetic()

        // when
        val rs = client.submitForm("/clubs") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("name", name)
                    }
                )
            )
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(">$name<")
        repo().queryOne("select count(*) from clubs where name = ?", listOf(text(name))) {
            assertThat(it.int(1)).isGreaterThan(0)
        }
    }

    @Test
    fun `should remove club`() = testApplication {
        // given
        application {
            serverModule()
        }
        val club = repo().access { a -> a.addClub(randomAlphabetic()) }

        // when
        val rs = client.delete("/clubs/${club.id}")

        // then
        assertThat(rs.status).isEqualTo(OK)
        repo().queryOne("select count(*) from clubs where id = ?", listOf(uuid(club.id))) {
            assertThat(it.int(1)).isEqualTo(0)
        }
    }
}