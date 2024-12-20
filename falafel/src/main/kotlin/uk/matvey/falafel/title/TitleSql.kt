package uk.matvey.falafel.title

import uk.matvey.falafel.FalafelSql.FALAFEL
import uk.matvey.falafel.title.Title.State.ACTIVE
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.kit.json.JsonKit.jsonObjectEncode
import uk.matvey.slon.RecordReader
import uk.matvey.slon.access.Access
import uk.matvey.slon.access.AccessKit.queryOne
import uk.matvey.slon.query.InsertOneQueryBuilder.Companion.insertOneInto
import uk.matvey.slon.query.ReturningQuery.Companion.returning
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll
import uk.matvey.slon.value.Pg
import uk.matvey.slon.value.PgText.Companion.toPgText
import uk.matvey.slon.value.PgUuid.Companion.toPgUuid
import java.time.Year
import java.util.UUID

object TitleSql {

    const val TITLES = "$FALAFEL.titles"

    const val ID = "id"
    const val STATE = "state"
    const val DIRECTOR_NAME = "director_name"
    const val RELEASE_YEAR = "release_year"
    const val REFS = "refs"
    const val TMDB_ID = "($REFS -> 'tmdb')::int"
    const val TITLE = "title"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"

    fun Access.getTitle(id: UUID): Title {
        return queryOne("select * from $TITLES where $ID = ?", listOf(id.toPgUuid()), ::readTitle)
    }

    fun Access.addTitle(
        title: String,
        directorName: String?,
        year: Int?,
        tmdbId: Int,
        tmdbPosterPath: String?
    ): Title {
        return query(
            insertOneInto(TITLES)
                .set(STATE, ACTIVE)
                .set(TITLE, title)
                .set(DIRECTOR_NAME, directorName)
                .set(RELEASE_YEAR, year)
                .set(UPDATED_AT, Pg.now())
                .set(REFS, jsonObjectEncode(Title.Refs(tmdbId, tmdbPosterPath)))
                .build()
                .returning(::readTitle)
        ).single()
    }

    fun Repo.searchActiveTitles(query: String): List<Title> {
        return queryAll(
            """
                |select * from $TITLES
                | where $STATE = ?
                | and $TITLE ilike ?
                | order by $CREATED_AT desc
                | limit 10
                |""".trimMargin(),
            listOf(
                ACTIVE.name.toPgText(),
                query.split(' ').joinToString(prefix = "%", separator = "%", postfix = "%").toPgText(),
            ),
            ::readTitle
        )
    }

    fun Repo.findAllByTmbdIds(tmdbIds: List<Int>): List<Title> {
        return queryAll(
            """
                |select * from $TITLES
                |   where $TMDB_ID in (${tmdbIds.joinToString(",")})
            """.trimMargin(),
            ::readTitle
        )
    }

    fun readTitle(reader: RecordReader): Title {
        return Title(
            id = reader.uuid(ID),
            state = Title.State.valueOf(reader.string(STATE)),
            title = reader.string(TITLE),
            directorName = reader.stringOrNull(DIRECTOR_NAME),
            releaseYear = reader.intOrNull(RELEASE_YEAR)?.let(Year::of),
            refs = jsonDeserialize(reader.string(REFS)),
            createdAt = reader.instant(CREATED_AT),
            updatedAt = reader.instant(UPDATED_AT),
        )
    }
}
