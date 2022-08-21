package com.rohan.newsexplorer.ui.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
    Device Reboot clears all the scheduled alarms
    So Reschedule on Reboot.
 */

class BootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            context?.let { AlarmHelper.scheduleWorkerUsingAlarm(it) }
        }

    }
}