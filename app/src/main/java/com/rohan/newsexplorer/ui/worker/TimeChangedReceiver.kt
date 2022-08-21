package com.rohan.newsexplorer.ui.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
    User changing device's timing clears all the scheduled alarms
    So Reschedule on TimeSet event.
 */

class TimeChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.TIME_SET") {
            context?.let { AlarmHelper.scheduleWorkerUsingAlarm(it) }
        }

    }
}