package uk.matvey.begit.tg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import uk.matvey.begit.TestContainerSetup
import uk.matvey.begit.tg.TgSessionSql.ensureTgSession
import uk.matvey.dukt.random.RandomSupport.randomLong
import uk.matvey.slon.Repo

class TgSessionSqlTest : TestContainerSetup() {

    @Test
    fun `should insert tg session`() {
        // given
        val chatId = randomLong()

        // when
        val result = repo.access { a -> a.ensureTgSession(chatId) }

        // then
        assertThat(result.chatId).isEqualTo(chatId)
    }

    companion object {

        private lateinit var repo: Repo

        @BeforeAll
        @JvmStatic
        fun initSetup() {
            repo = Repo(dataSource())
        }
    }
}