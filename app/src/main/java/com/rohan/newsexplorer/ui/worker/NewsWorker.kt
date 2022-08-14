package com.rohan.newsexplorer.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class NewsWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(applicationContext, workerParameters) {

    override suspend fun doWork(): Result {
        Log.d("Worker", "Worker Started")


        return Result.success()
    }
}