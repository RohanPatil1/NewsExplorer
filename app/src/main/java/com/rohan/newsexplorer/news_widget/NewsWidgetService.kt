package com.rohan.newsexplorer.news_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.rohan.newsexplorer.R
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.repository.NewsRepository
import com.rohan.newsexplorer.utils.DataResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/*
App widget is in different process from the main app so we have to use Remote Views
Therefore we cant use the normal adapter as well, therefore we use RemoteViewsFactory
 */

@AndroidEntryPoint
class NewsWidgetService : RemoteViewsService() {

    @Inject
    lateinit var newsRepository: NewsRepository

    @Inject
    lateinit var appContext: Context

    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return NewsWidgetItemFactory(applicationContext, intent!!)
    }


    inner class NewsWidgetItemFactory(private var mContext: Context, intent: Intent) :
        RemoteViewsFactory {

        private var appWidgetId: Int = 0
        var dataList = listOf<NData>()

        init {
            this.appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }


        override fun onCreate() {
            //Connect to database
            //Prepare Data
            when (val d = newsRepository.fetchDownloadedNews()) {
                is DataResult.Error -> {
                    //Don't show notification
                    Log.d("NewsWidget", " on Create NO DATA")
                    return
                }
                is DataResult.Success -> {
                    dataList = d.data.newDataList
                }
            }
        }

        override fun onDataSetChanged() {
            when (val d = newsRepository.fetchDownloadedNews()) {
                is DataResult.Error -> {
                    //Don't show notification
                    Log.d("NewsWidget", " onDataSetChanged NO DATA")
                    return
                }
                is DataResult.Success -> {
                    dataList = d.data.newDataList

                }
            }
            getViewAt(0)
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return dataList.size
        }

        override fun getViewAt(position: Int): RemoteViews {

            val views: RemoteViews = RemoteViews(mContext.packageName, R.layout.widget_item_layout)
            views.setTextViewText(R.id.stackItemTitle, dataList[position].title)
//            try {
//                val glideWidgetTarget = AppWidgetTarget(
//                    appContext,
//                    R.id.stackItemImage,
//                    views,appWidgetId
//                )
//                Glide
//                    .with(appContext)
//                    .asBitmap()
//                    .load(dataList[position].imageUrl)
//                    .into(glideWidgetTarget)
//            } catch (e: Exception) {
//                    Log.d("NewsWidget","GLIDE ERROR")
//                    Log.d("NewsWidget","${e.message}")
//            }
            try {
                val bitmap: Bitmap = Glide.with(appContext)
                    .asBitmap()
                    .load(dataList[position].imageUrl)
                    .submit(220, 180)
                    .get()
                views.setImageViewBitmap(R.id.stackItemImage, bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("NewsWidget", "GLIDE ERROR")
                Log.d("NewsWidget", "${e.message}")
            }



            return views
        }

        override fun getLoadingView(): RemoteViews? {
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(pos: Int): Long {
            return pos.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }
}

