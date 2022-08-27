package com.rohan.newsexplorer.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rohan.newsexplorer.data.repository.NewsRepository
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification
import com.rohan.newsexplorer.utils.DataResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/*
    Show News Updates Notification Periodically
 */

@HiltWorker
class NewsWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val newsUpdatesNotification: NewsUpdatesNotification,
    private val newsRepository: NewsRepository
) : CoroutineWorker(applicationContext, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker Started")
//        val b = AutoStartPermissionHelper.getInstance()
//            .isAutoStartPermissionAvailable(applicationContext)

        //Prepare Data
        val dataForNotification = withContext(Dispatchers.IO) {
            when (val d = newsRepository.fetchNewsData("all", forceRefresh = true)) {
                is DataResult.Error -> {
                    //Don't show notification
                    Log.d(TAG, "NO Notification")
                    null
                }
                is DataResult.Success -> {
                    Log.d(TAG, "SUCCESS GOT DATA")
                    val dataList = d.data.newDataList
                    dataList[(0..dataList.size).random()]
                }
            }
        }

        dataForNotification?.let {
            Log.d(TAG, "Non Null Data")
            newsUpdatesNotification.showNotification(it)
        }

        if (dataForNotification == null) {
            Log.d(TAG, "Null Data")
            return Result.failure()
        }
        return Result.success()
    }

    companion object {
        const val TAG = "NewsWorker"
    }
}