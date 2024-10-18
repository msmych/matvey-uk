package uk.matvey.falafel

import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.impl.StdSchedulerFactory
import uk.matvey.falafel.balance.TopupBalanceJob
import uk.matvey.slon.repo.Repo

class FalafelJobs(
    repo: Repo,
) {

    private val jobs = mapOf(
        TopupBalanceJob(repo) to EVERY_HOUR,
    )

    private val scheduler = StdSchedulerFactory.getDefaultScheduler()


    init {
        scheduler.setJobFactory { bundle, scheduler ->
            jobs.keys.find {
                bundle.jobDetail.jobClass == it.javaClass
            }
        }
        jobs.forEach { (job, trigger) ->
            scheduler.scheduleJob(
                newJob(job::class.java)
                    .withIdentity(job::class.simpleName, GROUP)
                    .build(),
                trigger
            )
        }
    }

    fun start() {
        scheduler.start()
    }

    companion object {

        const val GROUP = "falafel-jobs"

        private val EVERY_HOUR = newTrigger()
            .withIdentity("every-hour", GROUP)
            .startNow()
            .withSchedule(
                simpleSchedule()
                    .withIntervalInMinutes(12)
                    .repeatForever()
            )
            .build()
    }
}
