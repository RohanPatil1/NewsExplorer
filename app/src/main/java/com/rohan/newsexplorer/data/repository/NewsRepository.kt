package com.rohan.newsexplorer.data.repository


import android.util.Log
import com.rohan.newsexplorer.data.db.NewsDao
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.model.NewsData
import com.rohan.newsexplorer.data.network.NewsApiService
import com.rohan.newsexplorer.utils.DataResult
import java.lang.Exception
import javax.inject.Inject


class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDao: NewsDao,
) {

    //ForceRefresh - Fetch new data from api, clear & update cache
    suspend fun fetchNewsData(
        category: String,
        forceRefresh: Boolean = false
    ): DataResult<NewsData> {
        return try {

            //If not forceRefresh, try to return the cached data from DB
            if (!forceRefresh) {
                val cachedNews = newsDao.getNewsList()
                if (!cachedNews.isNullOrEmpty()) {
                    val nData =
                        NewsData(category = category, success = true, newDataList = cachedNews)
                    return DataResult.Success(nData)
                }
            }

            val response = newsApiService.getNewsData(category)

            if (response.isSuccessful) {
                //Clear DB
                newsDao.deleteAll()

                //Filter dataList by excluding null "readMoreUrl" entities
                val verifiedNewsList = arrayListOf<NData>()
                for (nData in response.body()!!.newDataList) {
                    if (nData.readMoreUrl == null) continue
                    verifiedNewsList.add(nData)
                }

                //Update DB
                newsDao.insertAll(verifiedNewsList.toList())
                DataResult.Success(response.body()!!)
            } else {
                response.errorBody()?.toString()?.let { Log.d(TAG, it) }
                DataResult.Error(Exception(response.errorBody()?.toString() ?: ""))
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            if (e.message.toString().contains("Unable to resolve host"))
                DataResult.Error(Exception("You're offline"))
            else
                DataResult.Error(e)
        }
    }

    //Fetch News For Discover Section
    suspend fun fetchDiscoverNewsData(category: String): DataResult<NewsData> {
        return try {
            val response = newsApiService.getNewsData(category)
            if (response.isSuccessful) {
                val verifiedNewsList = arrayListOf<NData>()
                for (nData in response.body()!!.newDataList) {
                    if (nData.readMoreUrl == null) continue
                    verifiedNewsList.add(nData)
                }
                DataResult.Success(response.body()!!)
            } else {
                response.errorBody()?.toString()?.let { Log.d(TAG, it) }
                DataResult.Error(Exception(response.errorBody()?.toString() ?: ""))
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            if (e.message.toString().contains("Unable to resolve host"))
                DataResult.Error(Exception("You're offline"))
            else
                DataResult.Error(e)
        }
    }

    fun fetchDownloadedNews(): DataResult<NewsData> {
        return try {
            val cachedNews = newsDao.getNewsList()
            return if (cachedNews.isNullOrEmpty()) {
                Log.d(TAG, "DB IS NULL")
                DataResult.Error(Exception("You don't have any downloaded news"))
            } else {
                Log.d(TAG, "DB IS  NOT NULL")
                val nData =
                    NewsData(category = "downloads", success = true, newDataList = cachedNews)
                DataResult.Success(nData)
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            if (e.message.toString().contains("Unable to resolve host"))
                DataResult.Error(Exception("No internet connection..."))
            else
                DataResult.Error(e)
        }
    }

    suspend fun saveNewsToDb(news: NData) {
        try {
            newsDao.insert(news)
        } catch (e: Exception) {
            Log.d(TAG, "Could not save")
        }
    }

    fun deleteNews(news: NData) {
        try {
            newsDao.deleteNews(news)
        } catch (e: Exception) {
            Log.d(TAG, "Could not save")
        }
    }

    companion object {
        const val TAG = "NewsRepository"
    }

}