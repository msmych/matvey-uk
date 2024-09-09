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
import uk.matvey.corsa.event.EventSql.DATE_TIME
import uk.matvey.corsa.event.EventSql.NAME
import uk.matvey.corsa.event.EventSql.addEvent
import uk.matvey.kit.random.RandomKit.randomAlphabetic
import uk.matvey.kit.time.TimeKit.localDate
import uk.matvey.kit.time.TimeKit.localTime
import uk.matvey.slon.query.Query.Companion.plainQuery
import uk.matvey.slon.value.PgText.Companion.toPgText
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import uk.matvey.utka.ktor.KtorKit.setFormData
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit.MILLIS
import java.util.UUID.randomUUID

class EventResourceTest : TestSetup() {

    @Test
    fun `should return new event form`() = testApp { client ->
        // given
        val clubId = randomUUID()

        // when
        val rs = client.get("/events/new-event-form?clubId=$clubId")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains("<h3>New event</h3>")
    }

    @Test
    fun `should add event`() = testApp { client ->
        // given
        val club = repo.access { a -> a.addClub(randomAlphabetic(), randomUUID()) }
        val name = randomAlphabetic()
        val date = localDate()
        val time = localTime()

        // when
        val rs = client.submitForm("/events") {
            setFormData(
                "clubId" to club.id.toString(),
                "name" to name,
                "date" to date.toString(),
                "time" to time.toString(),
            )
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).contains(">$name<")

        repo.access { a ->
            a.query(plainQuery("select * from events where name = ?", listOf(name.toPgText())) { r ->
                assertThat(r.uuid(CLUB_ID)).isEqualTo(club.id)
                assertThat(r.string(NAME)).isEqualTo(name)
                assertThat(r.localDate(DATE)).isEqualTo(date)
                assertThat(r.instant(DATE_TIME).truncatedTo(MILLIS))
                    .isEqualTo(LocalDateTime.of(date, time).toInstant(UTC).truncatedTo(MILLIS))
            })
        }
    }

    @Test
    fun `should remove event`() = testApp { client ->
        // given
        val event = repo.access { a -> a.addEvent(randomUUID(), randomAlphabetic(), localDate(), localTime()) }

        // when
        val rs = client.delete("/events/${event.id}") {}

        // then
        assertThat(rs.status).isEqualTo(OK)

        repo.access { a ->
            a.query(
                plainQuery("select count(*) from events where id = ?", listOf(event.id.toPgUuid())) {
                    assertThat(it.int(1)).isEqualTo(0)
                }
            )
        }
    }
}
