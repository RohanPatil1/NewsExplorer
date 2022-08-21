package com.rohan.newsexplorer.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay


/*
    Show News Updates Notification Periodically
 */

@HiltWorker
class NewsWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val newsUpdatesNotification: NewsUpdatesNotification
) : CoroutineWorker(applicationContext, workerParameters) {


    override suspend fun doWork(): Result {
        Log.d("Worker", "Worker Started")
        val b = AutoStartPermissionHelper.getInstance()
            .isAutoStartPermissionAvailable(applicationContext)
        Log.d("Worker", "AUTO  Start is : $b")

        newsUpdatesNotification.showNotification()
        return Result.success()
    }
}