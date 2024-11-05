package uk.matvey.falafel

import kotlinx.coroutines.flow.MutableSharedFlow
import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import uk.matvey.falafel.balance.IncrementBalancesJob
import uk.matvey.slon.repo.Repo
import java.util.UUID

class FalafelJobs(
    repo: Repo,
    balanceEvents: MutableMap<UUID, MutableSharedFlow<Int>>,
) {

    val incrementBalancesJob = IncrementBalancesJob(repo, balanceEvents)

    val jobs = listOf(incrementBalancesJob)
        .associateBy { job -> job::class.java }

    private val jobsTriggers = mapOf(
        incrementBalancesJob to EVERY_HOUR,
    )

    private val scheduler = StdSchedulerFactory.getDefaultScheduler()

    init {
        scheduler.setJobFactory { bundle, _ -> jobs[bundle.jobDetail.jobClass] }
        jobsTriggers.forEach { (job, trigger) ->
            scheduler.scheduleJob(
                newJob(job::class.java)
                    .withIdentity(job::class.simpleName, FALAFEL_JOBS)
                    .build(),
                trigger
            )
        }
    }

    fun start() {
        scheduler.start()
    }

    companion object {

        const val FALAFEL_JOBS = "falafel-jobs"

        private val EVERY_HOUR = newTrigger()
            .withIdentity("every-hour", FALAFEL_JOBS)
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInHours(1)
                    .repeatForever()
            )
            .build()
    }
}
