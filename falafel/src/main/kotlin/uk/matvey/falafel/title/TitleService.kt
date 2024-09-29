package uk.matvey.falafel.title

import kotlinx.coroutines.coroutineScope
import uk.matvey.falafel.title.TitleSql.TITLES
import uk.matvey.falafel.title.TitleSql.readTitle
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.queryAll

class TitleService(
    private val repo: Repo,
) {

    suspend fun getTitles(): List<Title> = coroutineScope {
        repo.queryAll("select * from $TITLES", ::readTitle)
    }
}
