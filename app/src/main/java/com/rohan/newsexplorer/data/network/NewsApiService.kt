package com.rohan.newsexplorer.data.network

import com.rohan.newsexplorer.data.model.NewsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("news")
    suspend fun getNewsData(@Query("category") category: String="science"): Response<NewsData>

}