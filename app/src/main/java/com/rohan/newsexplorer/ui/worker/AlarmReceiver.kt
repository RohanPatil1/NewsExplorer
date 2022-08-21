package com.rohan.newsexplorer.ui.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.rohan.newsexplorer.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
//    @Inject
//    lateinit var newsUpdatesNotification: NewsUpdatesNotification

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Constants.DAILY_8AM_NOTIFICATION) {

            val request = OneTimeWorkRequest.Builder(NewsWorker::class.java).build()
            WorkManager.getInstance(context!!.applicationContext)
                .enqueue(request)
            Log.d("Worker", "Worker Enqueued")
//            newsUpdatesNotification.showNotification()
        }
    }
}