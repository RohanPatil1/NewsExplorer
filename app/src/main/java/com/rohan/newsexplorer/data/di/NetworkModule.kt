package com.rohan.newsexplorer.data.di

import android.content.Context
import com.rohan.newsexplorer.data.network.NewsApiService
import com.rohan.newsexplorer.utils.ConnectionLiveData
import com.rohan.newsexplorer.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .run {
                baseUrl(BASE_URL)
                addConverterFactory(GsonConverterFactory.create())
                build()
            }
    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideConnectionLivedata(@ApplicationContext context: Context): ConnectionLiveData {
        return ConnectionLiveData(context)
    }
}