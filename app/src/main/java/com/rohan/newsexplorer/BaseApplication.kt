package com.rohan.newsexplorer

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.rohan.newsexplorer.ui.notification.NewsUpdatesNotification.Companion.NEWS_CHANNEL_ID
import com.rohan.newsexplorer.ui.worker.AlarmReceiver
import com.rohan.newsexplorer.utils.Constants.DAILY_8AM_NOTIFICATION
import com.rohan.newsexplorer.utils.Constants.NOTIFICATION_ALARM_REQ_ID
import dagger.hilt.android.HiltAndroidApp
import java.util.*
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Log.d("Application", "Application onCreate()")
        AutoStartPermissionHelper.getInstance().getAutoStartPermission(applicationContext)
        val b = AutoStartPermissionHelper.getInstance()
            .isAutoStartPermissionAvailable(applicationContext)
        Log.d("Application", "AUTO  Start is : $b")
        createNotificationChannel()
        scheduleWorkerUsingAlarm()
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

    private fun scheduleWorkerUsingAlarm() {
        val alarmManger = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
        alarmIntent.action = DAILY_8AM_NOTIFICATION
        val alarmPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            NOTIFICATION_ALARM_REQ_ID,
            alarmIntent,
            0
        )

        //Prepare Schedule
        val calendar: Calendar = Calendar.getInstance()
        val now: Calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        /*
        Check whether the time is earlier than current time.
        If so, set it to tomorrow, Otherwise all alarms of earlier time will fire
         */
        if (calendar.before(now))
            calendar.add(Calendar.DATE, 1)

        //Set Alarm for 8AM EveryDay
        alarmManger.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, alarmPendingIntent
        )

        //Test
        //        alarmManger.setInexactRepeating(
        //            AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+1000,
        //            60*1000, alarmPendingIntent
        //        )

    }
}