package com.rohan.newsexplorer.ui.notification

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.widget.RemoteViews
import androidx.navigation.NavDeepLinkBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.ui.fragments.DetailsFragmentArgs
import com.rohan.newsexplorer.utils.Constants.DAILY_NEWS_NOTIFICATION_ID
import javax.inject.Inject


class NewsUpdatesNotification @Inject constructor(
    private val context: Context
) {
    companion object {
        const val NEWS_CHANNEL_ID = "NewsChannel"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(dataForNotification: NData) {

        //Prepare Views (Small & Big)
        val collapsedRemoteView =
            RemoteViews(context.packageName, R.layout.notification_collapsed_layout)
        val expandedRemoteView =
            RemoteViews(context.packageName, R.layout.notification_expanded_layout)

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
        notificationManager.notify(DAILY_NEWS_NOTIFICATION_ID, notification)
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