package uk.matvey.begit.club

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import uk.matvey.begit.TestContainerSetup
import uk.matvey.begit.club.ClubSql.CLUBS
import uk.matvey.begit.club.ClubSql.CLUB_ID
import uk.matvey.begit.club.ClubSql.CLUB_MEMBERS
import uk.matvey.begit.club.ClubSql.CREATED_AT
import uk.matvey.begit.club.ClubSql.ID
import uk.matvey.begit.club.ClubSql.NAME
import uk.matvey.begit.club.ClubSql.REFS
import uk.matvey.begit.club.ClubSql.UPDATED_AT
import uk.matvey.dukt.random.RandomSupport.randomLong
import uk.matvey.dukt.random.RandomSupport.randomStr
import uk.matvey.slon.Repo
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid

class ClubServiceTest : TestContainerSetup() {

    @Test
    fun `should add club`() {
        // given
        val tgId = randomLong()

        // when
        val (club, count) = clubService.ensureClub("club1", tgId)

        // then
        assertThat(club.name).isEqualTo("club1")
        assertThat(club.refs.tgId).isEqualTo(tgId)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should update club name if already exists`() {
        // given
        val tgId = randomLong()

        repo.insertOne(
            CLUBS,
            ID to genRandomUuid(),
            NAME to text("club1"),
            REFS to jsonb(Json.encodeToString(Club.Refs(tgId))),
            CREATED_AT to now(),
            UPDATED_AT to now(),
        )

        // when
        val (club, count) = clubService.ensureClub("club2", tgId)

        // then
        assertThat(club.name).isEqualTo("club2")
        assertThat(club.refs.tgId).isEqualTo(tgId)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should add member to club`() {
        // given
        val clubTgId = randomLong()
        val memberTgId = randomLong()
        val memberName = randomStr(10)
        val (club, _) = clubService.ensureClub("club1", clubTgId)

        // when
        clubService.addClubMember(club.id, memberTgId, memberName)

        // then
        val result = repo.queryOneNullable(
            "select * from $CLUB_MEMBERS where $CLUB_ID = ?",
            listOf(uuid(club.id))
        ) {}

        assertThat(result).isNotNull
    }

    companion object {

        private lateinit var repo: Repo
        private lateinit var clubService: ClubService

        @BeforeAll
        @JvmStatic
        fun initSetup() {
            repo = Repo(dataSource())
            clubService = ClubService(repo)
        }
    }
}