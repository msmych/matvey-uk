package uk.matvey.falafel.balance

import org.quartz.Job
import org.quartz.JobExecutionContext
import uk.matvey.falafel.balance.BalanceSql.topupBalance
import uk.matvey.falafel.tag.TagSql.findAllBalanceIdsOfTagsAddedAfter
import uk.matvey.kit.random.RandomKit.randomInt
import uk.matvey.slon.repo.Repo
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class TopupBalanceJob(private val repo: Repo) : Job {

    override fun execute(ctx: JobExecutionContext) {
        val cutoff = ctx.fireTime.toInstant().minus(16.hours.toJavaDuration())
        val tagsCounts = repo.access { a -> a.findAllBalanceIdsOfTagsAddedAfter(cutoff) }
        tagsCounts.filter { randomInt() % 4 == 0 }.forEach { balanceId ->
            repo.access { a ->
                a.topupBalance(balanceId)
            }
        }
    }
}
