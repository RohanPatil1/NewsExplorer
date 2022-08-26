package com.rohan.newsexplorer.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.repository.NewsRepository
import com.rohan.newsexplorer.ui.activity.MainActivity
import com.rohan.newsexplorer.ui.fragments.DetailsFragmentArgs
import com.rohan.newsexplorer.utils.DataResult
import javax.inject.Inject


class NewsUpdatesNotification @Inject constructor(
    private val context: Context
) {
    companion object {
        const val NEWS_CHANNEL_ID = "NewsChannel"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // NOTE - This will be called on the WorkerThread and not the MainThread
    fun showNotification(dataForNotification: NData) {

        //Prepare Views (Small & Big)
        val collapsedRemoteView =
            RemoteViews(context.packageName, R.layout.notification_collapsed_layout)
        val expandedRemoteView =
            RemoteViews(context.packageName, R.layout.notification_expanded_layout)


        //Click event using PendingIntent
//        val activityIntent = Intent(context, MainActivity::class.java)
//        val activityPendingIntent = PendingIntent.getActivity(
//            context,
//            1,
//            activityIntent,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
//        )

        //Send To Details Screen onClick of notification using Deeplink
        val detailsPendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.details_fragment)
            .setArguments(DetailsFragmentArgs(dataForNotification).toBundle())
            .createPendingIntent()

        //Notification Builder
        val notification = Notification.Builder(context, NEWS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle("Daily News Updates")
            .setContentIntent(detailsPendingIntent)
            .setCustomContentView(collapsedRemoteView)
//            .setContent(collapsedRemoteView)
//            .setCustomBigContentView(expandedRemoteView)
            .build()


        setCollapsedView(collapsedRemoteView, dataForNotification, notification)
        setExpandedView(expandedRemoteView, dataForNotification, notification)
        notificationManager.notify(1, notification)
    }

    private fun setCollapsedView(
        collapsedView: RemoteViews,
        data: NData,
        notification: Notification
    ) {

        //Set data into textview
        collapsedView.setTextViewText(R.id.notifSmallTitleTV, data.title)
        collapsedView.setTextViewText(R.id.notifSmallSubTitleTV, data.content)

        //Set Image from Url into RemoteView using NotificationTarget
        val notificationTarget = NotificationTarget(
            context,
            R.id.notifSmallIV,
            collapsedView,
            notification,
            1
        )
        Glide.with(context)
            .asBitmap()
            .load(data.imageUrl)
            .into<NotificationTarget>(notificationTarget)

    }

    private fun setExpandedView(
        expandedView: RemoteViews,
        data: NData,
        notification: Notification
    ) {

        //Set data into textview
        expandedView.setTextViewText(R.id.notifBigTitleTV, data.title)
        expandedView.setTextViewText(R.id.notifBigSubTitleTV, data.content)

        //Set Image from Url into RemoteView using NotificationTarget
        val notificationTarget = NotificationTarget(
            context,
            R.id.notifBigIV,
            expandedView,
            notification,
            1
        )
        Glide.with(context)
            .asBitmap()
            .load(data.imageUrl)
            .into<NotificationTarget>(notificationTarget)
    }

}