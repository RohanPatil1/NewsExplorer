package com.rohan.newsexplorer.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.repository.NewsRepository
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification
import com.rohan.newsexplorer.utils.DataResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject


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
        Log.d("Worker", "Worker Started")
//        val b = AutoStartPermissionHelper.getInstance()
//            .isAutoStartPermissionAvailable(applicationContext)

        //Prepare Data
        val dataForNotification = prepareData()

        dataForNotification?.let {
            Log.d("Worker", "Non Null Data")
            newsUpdatesNotification.showNotification(it)
        }

        if (dataForNotification == null) {
            Log.d("Worker", "Null Data")

            return Result.retry()
        }
        return Result.success()
    }


    private suspend fun prepareData(): NData? {
        lateinit var dataList: List<NData>

        //ForceRefresh - Fetch new data from api , clear & update cache
        when (val d = newsRepository.fetchNewsData("all", forceRefresh = true)) {
            is DataResult.Error -> {
                //Don't show notification
                Log.d("Worker", "NO Notification")
                return null
            }
            is DataResult.Success -> {
                Log.d("Worker", "SUCCESS")
                dataList = d.data.newDataList
            }
        }

        //Data shown in notifications
        val randomIndex: Int = (0..dataList.size).random()
        return dataList[randomIndex]
    }
}