package uk.matvey.corsa.club

import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.corsa.TestSetup
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.kit.random.RandomKit.randomAlphabetic
import uk.matvey.kit.random.RandomKit.randomName
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.voron.KtorKit.setFormData
import java.util.UUID.randomUUID

class ClubResourceTest : TestSetup() {

    @Test
    fun `should return clubs`() = testApp { client ->
        // given
        val club1 = repo.access { a -> a.addClub(randomName(), athleteId) }
        val club2 = repo.access { a -> a.addClub(randomName(), athleteId) }

        // when
        val rs = client.get("/clubs")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(">${club1.name}<")
            .contains(">${club2.name}<")
    }

    @Test
    fun `should return new club form`() = testApp { client ->
        // when
        val rs = client.get("/clubs/new-club-form")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains(">New club<")
    }

    @Test
    fun `should add club`() = testApp { client ->
        // given
        val name = randomAlphabetic()

        // when
        val rs = client.submitForm("/clubs") {
            setFormData("name" to name)
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains(">$name<")
        repo.queryOne("select count(*) from clubs where name = ?", listOf(text(name))) {
            assertThat(it.int(1)).isGreaterThan(0)
        }
    }

    @Test
    fun `should remove club`() = testApp { client ->
        // given
        val club = repo.access { a -> a.addClub(randomAlphabetic(), randomUUID()) }

        // when
        val rs = client.delete("/clubs/${club.id}")

        // then
        assertThat(rs.status).isEqualTo(OK)
        repo.queryOne("select count(*) from clubs where id = ?", listOf(uuid(club.id))) {
            assertThat(it.int(1)).isEqualTo(0)
        }
    }

    @Test
    fun `should return club details`() = testApp { client ->
        // given
        val club = repo.access { a -> a.addClub(randomAlphabetic(), randomUUID()) }

        // when
        val rs = client.get("/clubs/${club.id}")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText())
            .contains("No events")
    }
}
