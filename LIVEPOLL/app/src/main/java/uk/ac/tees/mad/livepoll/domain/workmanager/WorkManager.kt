package uk.ac.tees.mad.livepoll.domain.workmanager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun schedulePollStatusUpdate(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<PollStatusWorker>(15, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "PollStatusUpdateWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
}