package uk.matvey.corsa.event

import io.ktor.client.request.delete
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.corsa.TestSetup
import uk.matvey.corsa.club.ClubSql.addClub
import uk.matvey.corsa.event.EventSql.CLUB_ID
import uk.matvey.corsa.event.EventSql.DATE
import uk.matvey.corsa.event.EventSql.NAME
import uk.matvey.corsa.event.EventSql.addEvent
import uk.matvey.kit.random.RandomKit.randomAlphabetic
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.RepoKit.queryOne
import uk.matvey.voron.KtorKit.setFormData
import java.time.LocalDate
import java.util.UUID.randomUUID

class EventResourceTest : TestSetup() {

    @Test
    fun `should return new event form`() = testApp {
        // given
        val clubId = randomUUID()

        // when
        val rs = client.get("/events/new-event-form?clubId=$clubId")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains("<h3>New event</h3>")
    }

    @Test
    fun `should add event`() = testApp {
        // given
        val club = repo.access { a -> a.addClub(randomAlphabetic()) }
        val name = randomAlphabetic()
        val date = LocalDate.now()

        // when
        val rs = client.submitForm("/events") {
            setFormData(
                "clubId" to club.id.toString(),
                "name" to name,
                "date" to date.toString()
            )
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains(">$name<")

        repo.queryOne("select * from events where name = ?", listOf(text(name))) { r ->
            assertThat(r.uuid(CLUB_ID)).isEqualTo(club.id)
            assertThat(r.string(NAME)).isEqualTo(name)
            assertThat(r.localDate(DATE)).isEqualTo(date)
        }
    }

    @Test
    fun `should remove event`() = testApp {
        // given
        val event = repo.access { a -> a.addEvent(randomUUID(), randomAlphabetic(), LocalDate.now()) }

        // when
        val rs = client.delete("/events/${event.id}") {}

        // then
        assertThat(rs.status).isEqualTo(OK)

        repo.queryOne("select count(*) from events where id = ?", listOf(uuid(event.id))) {
            assertThat(it.int(1)).isEqualTo(0)
        }
    }
}