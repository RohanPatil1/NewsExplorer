package com.rohan.newsexplorer.ui.worker

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.rohan.newsexplorer.utils.Constants
import java.util.*

/*
    Scheduling Alarms helper
 */

object AlarmHelper {

    //Schedule Worker to show notification Everyday 8AM
    fun scheduleWorkerUsingAlarm(context: Context) {
        val alarmManger = context.getSystemService(Application.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        alarmIntent.action = Constants.DAILY_8AM_NOTIFICATION
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            Constants.NOTIFICATION_ALARM_REQ_ID,
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
        //alarmManger.setInexactRepeating(
        //AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,
        //60 * 1000, alarmPendingIntent
        // )

    }

}