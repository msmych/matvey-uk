package uk.matvey.begit.club

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import uk.matvey.begit.TestContainerSetup
import uk.matvey.begit.club.ClubMemberSql.CLUB_ID
import uk.matvey.begit.club.ClubMemberSql.CLUB_MEMBERS
import uk.matvey.begit.club.ClubSql.CLUBS
import uk.matvey.begit.club.ClubSql.CREATED_AT
import uk.matvey.begit.club.ClubSql.ID
import uk.matvey.begit.club.ClubSql.NAME
import uk.matvey.begit.club.ClubSql.REFS
import uk.matvey.begit.club.ClubSql.UPDATED_AT
import uk.matvey.slon.param.JsonbParam.Companion.jsonb
import uk.matvey.slon.param.PlainParam.Companion.genRandomUuid
import uk.matvey.slon.param.PlainParam.Companion.now
import uk.matvey.slon.param.TextParam.Companion.text
import uk.matvey.slon.param.UuidParam.Companion.uuid
import uk.matvey.slon.repo.Repo
import uk.matvey.slon.repo.RepoKit.insertInto
import uk.matvey.slon.repo.RepoKit.queryOneNullable
import kotlin.random.Random

class ClubServiceTest : TestContainerSetup() {

    @Test
    fun `should add club`() = runTest {
        // given
        val tgChatId = Random.Default.nextLong()

        // when
        val (club, count) = clubService.ensureClub("club1", tgChatId)

        // then
        assertThat(club.name).isEqualTo("club1")
        assertThat(club.refs.tgChatId).isEqualTo(tgChatId)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should update club name if already exists`() = runTest {
        // given
        val tgChatId = Random.Default.nextLong()

        repo.insertInto(CLUBS) {
            set(
                ID to genRandomUuid(),
                NAME to text("club1"),
                REFS to jsonb(Json.encodeToString(Club.Refs(tgChatId))),
                CREATED_AT to now(),
                UPDATED_AT to now(),
            )
        }

        // when
        val (club, count) = clubService.ensureClub("club2", tgChatId)

        // then
        assertThat(club.name).isEqualTo("club2")
        assertThat(club.refs.tgChatId).isEqualTo(tgChatId)
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `should add member to club`() = runTest {
        // given
        val clubTgChatId = Random.Default.nextLong()
        val athleteTgChatId = Random.Default.nextLong()
        val athleteName = "athlete1"
        val (club, _) = clubService.ensureClub("club1", clubTgChatId)
        val refs = buildJsonObject {
            put("tgChatId", Random.Default.nextLong())
            put("tgMessageId", Random.Default.nextLong())
        }

        // when
        clubService.addClubMember(club.id, athleteTgChatId, athleteName, refs)

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