package uk.matvey.falafel.balance

import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import uk.matvey.falafel.balance.BalanceSql.incrementBalances
import uk.matvey.slon.repo.Repo

class IncrementBalancesJob(
    private val repo: Repo,
    private val balanceEvents: BalanceEvents,
) : Job {

    override fun execute(ctx: JobExecutionContext) {
        val updatedBalances = repo.access { a -> a.incrementBalances() }
        runBlocking {
            updatedBalances.forEach { (accountId, current) ->
                balanceEvents.push(accountId, current)
            }
        }
    }
}
