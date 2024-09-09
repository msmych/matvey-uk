package uk.matvey.falafel.title

import uk.matvey.falafel.title.TitleSql.TITLES
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.slon.query.Query
import uk.matvey.slon.repo.Repo

class TitleService(
    private val repo: Repo,
) {

    suspend fun getTitles(): List<Title> {
        return repo.access { a ->
            a.query(
                Query.plainQuery("select * from $TITLES") {
                    Title(
                        id = it.uuid("id"),
                        state = Title.State.valueOf(it.string("state")),
                        title = it.string("title"),
                        clubId = it.uuidOrNull("club_id"),
                        refs = jsonDeserialize(it.string("refs")),
                        createdBy = it.uuidOrNull("created_by"),
                        createdAt = it.instant("created_at"),
                        updatedAt = it.instant("updated_at"),
                    )
                }
            )
        }
    }
}
