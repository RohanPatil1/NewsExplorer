package com.rohan.newsexplorer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification.Companion.NEWS_CHANNEL_ID
import com.rohan.newsexplorer.ui.worker.NewsWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "Application onCreate()")
        createNotificationChannel()
        scheduleBgWorker()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "NewsUpdates"
            val descriptionText = "Get regular news updates"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NEWS_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun scheduleBgWorker() {
        val request =
            PeriodicWorkRequest.Builder(NewsWorker::class.java, 4, TimeUnit.MINUTES).build()
        //Add request to queue
        WorkManager.getInstance(applicationContext)
            .enqueue(request)
 //      val requestId = request.id
//        //Observe
//        WorkManager.getInstance(applicationContext).getWorkInfoByIdLiveData(requestId)
//            .observe(this) {
//                val TAG = "Worker"
//                it?.let { workInfo ->
//                    when (workInfo.state) {
//                        WorkInfo.State.ENQUEUED ->
//                            Log.d(TAG, "Worker ENQUEUED")
//                        WorkInfo.State.RUNNING ->
//                            Log.d(TAG, "Worker RUNNING")
//                        WorkInfo.State.SUCCEEDED ->
//                            Log.d(TAG, "Worker SUCCEEDED")
//                        WorkInfo.State.FAILED ->
//                            Log.d(TAG, "Worker FAILED")
//                        WorkInfo.State.BLOCKED ->
//                            Log.d(TAG, "Worker BLOCKED")
//                        WorkInfo.State.CANCELLED ->
//                            Log.d(TAG, "Worker CANCELLED")
//                    }
//                }
//            }
    }
}