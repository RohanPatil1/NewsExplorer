package com.rohan.newsexplorer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.rohan.newsexplorer.data.repository.HandshakeRepository
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification.Companion.NEWS_CHANNEL_ID
import com.rohan.newsexplorer.ui.worker.AlarmHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "Application onCreate()")

        //Request "Auto-Start" permission, required for Chinese OEMs for smooth functioning of WorkManager
        //AutoStartPermissionHelper.getInstance().getAutoStartPermission(applicationContext)

        createNotificationChannel()

        //Initialize Remote Config
        HandshakeRepository.init()

        //Schedule Alarm to show notification Everyday 8AM
        AlarmHelper.scheduleWorkerUsingAlarm(applicationContext)
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

}