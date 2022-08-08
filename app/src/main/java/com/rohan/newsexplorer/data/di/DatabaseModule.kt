package com.rohan.newsexplorer.data.di

import android.app.Application
import androidx.room.Room
import com.rohan.newsexplorer.data.db.NewsDao
import com.rohan.newsexplorer.data.db.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application): NewsDatabase =
        Room.databaseBuilder(application, NewsDatabase::class.java, "newsDatabase")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDao(newsDatabase: NewsDatabase): NewsDao = newsDatabase.getDao()

}