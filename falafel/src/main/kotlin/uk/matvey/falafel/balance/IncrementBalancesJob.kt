package uk.matvey.falafel.balance

import org.quartz.Job
import org.quartz.JobExecutionContext
import uk.matvey.falafel.balance.BalanceSql.incrementBalances
import uk.matvey.slon.repo.Repo

class IncrementBalancesJob(
    private val repo: Repo,
) : Job {

    override fun execute(ctx: JobExecutionContext) {
        repo.access { a -> a.incrementBalances() }
    }
}
