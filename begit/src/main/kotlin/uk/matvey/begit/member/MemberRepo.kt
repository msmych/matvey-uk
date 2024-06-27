package uk.matvey.begit.member

import uk.matvey.begit.member.MemberSql.MEMBERS
import uk.matvey.begit.member.MemberSql.TG_ID
import uk.matvey.begit.member.MemberSql.readMember
import uk.matvey.slon.Repo
import uk.matvey.slon.param.TextParam.Companion.text

class MemberRepo(
    private val repo: Repo,
) {

    fun getByTgId(tgId: Long): Member {
        return repo.queryOne(
            "select * from $MEMBERS where $TG_ID = ?",
            listOf(text(tgId.toString()))
        ) { r -> r.readMember() }
    }
}