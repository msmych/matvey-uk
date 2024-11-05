package uk.matvey.falafel.balance

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import uk.matvey.falafel.balance.BalanceSql.incrementBalances
import uk.matvey.slon.repo.Repo
import java.util.UUID

class IncrementBalancesJob(
    private val repo: Repo,
    private val balanceEvents: MutableMap<UUID, MutableSharedFlow<Int>>,
) : Job {

    override fun execute(ctx: JobExecutionContext) {
        val updatedBalances = repo.access { a -> a.incrementBalances() }
        runBlocking {
            updatedBalances.forEach { (accountId, current) ->
                balanceEvents[accountId]?.emit(current)
            }
        }
    }
}
