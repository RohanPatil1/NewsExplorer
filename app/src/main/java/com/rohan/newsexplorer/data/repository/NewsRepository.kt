package com.rohan.newsexplorer.data.repository


import android.util.Log
import com.rohan.newsexplorer.data.db.NewsDao
import com.rohan.newsexplorer.data.model.NData
import com.rohan.newsexplorer.data.model.NewsData
import com.rohan.newsexplorer.data.network.NewsApiService
import com.rohan.newsexplorer.utils.DataResult
import com.rohan.newsexplorer.utils.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.Exception
import javax.inject.Inject


class NewsRepository @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDao: NewsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

) {
    suspend fun fetchNewsData(category: String): DataResult<NewsData> {
        return try {
//            val cachedNews = newsDao.getNewsList()
//            if (cachedNews.isNullOrEmpty()) {
//                Log.d("ROHANR", "DB IS NULL")
//            } else {
//                Log.d("ROHANR", "DB IS  NOT NULL")
//                val nData = NewsData(category = category, success = true, newDataList = cachedNews)
//                return DataResult.Success(nData)
//            }
//            Log.d("ROHANR", "API CALLING TO BE MADE")

            val response = newsApiService.getNewsData(category)

            if (response.isSuccessful) {
                newsDao.deleteAllNews(response.body()!!.newDataList)
                val verifiedNewsList = arrayListOf<NData>()
                for (nData in response.body()!!.newDataList) {
                    if (nData.readMoreUrl == null) continue
                    verifiedNewsList.add(nData)
                }
//                newsDao.insertAll(verifiedNewsList)
                DataResult.Success(response.body()!!)
            } else {
                response.errorBody()?.toString()?.let { Log.d("ROHAN", it) }
                DataResult.Error(Exception(response.errorBody()?.toString() ?: ""))
            }
        } catch (e: Exception) {
            Log.d("ROHAN", "GOT ERROR")
            Log.d("ROHAN", e.message.toString())
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
                Log.d("ROHAN", "[Downloads] DB IS NULL")
                DataResult.Error(Exception("You don't have any downloaded news"))
            } else {
                Log.d("ROHAN", "[Downloads] DB IS  NOT NULL")
                val nData =
                    NewsData(category = "downloads", success = true, newDataList = cachedNews)
                DataResult.Success(nData)
            }
        } catch (e: Exception) {
            Log.d("ROHAN", "[Downloads] GOT ERROR")
            Log.d("ROHAN", e.message.toString())
            if (e.message.toString().contains("Unable to resolve host"))
                DataResult.Error(Exception("No internet connection..."))
            else
                DataResult.Error(e)
        }

    }


    suspend fun downloadNews(news: NData) {
        try {
            newsDao.insert(news)
        } catch (e: Exception) {
            Log.d("ROHAN", "[Downloads] Could not save")
        }
    }


    fun deleteNews(news: NData) {
        try {
            newsDao.deleteNews(news)
        } catch (e: Exception) {
            Log.d("ROHAN", "[Downloads] Could not save")
        }
    }


//    suspend fun fetchDiscoverNewsData(category: String): DataResult<NewsData> {
//        return try {
//
//            val response = newsApiService.getNewsData(category)
//
//            if (response.isSuccessful) {
//                Log.d("ROHAN", "FETCHED ${response.body()!!.category}")
//
//                val verifiedNewsList = arrayListOf<NData>()
//                for (nData in response.body()!!.newDataList) {
//                    if (nData.readMoreUrl == null) continue
//
//                    verifiedNewsList.add(nData)
//                }
//                DataResult.Success(response.body()!!)
//            } else {
//                response.errorBody()?.toString()?.let { Log.d("ROHAN", it) }
//                DataResult.Error(Exception(response.errorBody()?.toString() ?: ""))
//            }
//        } catch (e: Exception) {
//            Log.d("ROHAN", "GOT ERROR")
//            Log.d("ROHAN", e.message.toString())
//
//            DataResult.Error(e)
//        }
//    }

}