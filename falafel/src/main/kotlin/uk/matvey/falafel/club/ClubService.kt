package uk.matvey.falafel.club

import uk.matvey.falafel.club.ClubSql.CLUBS
import uk.matvey.kit.json.JsonKit.jsonDeserialize
import uk.matvey.slon.access.AccessKit.queryAll
import uk.matvey.slon.repo.Repo

class ClubService(
    private val repo: Repo,
) {

    fun getClubs(): List<Club> {
        return repo.access { a ->
            a.queryAll(
                "select * from $CLUBS"
            ) {
                Club(
                    id = it.uuid("id"),
                    name = it.string("name"),
                    refs = jsonDeserialize(it.string("refs")),
                    createdAt = it.instant("created_at"),
                    updatedAt = it.instant("updated_at"),
                )
            }
        }
    }
}
