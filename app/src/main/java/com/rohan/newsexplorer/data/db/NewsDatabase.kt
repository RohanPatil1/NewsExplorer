package com.rohan.newsexplorer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rohan.newsexplorer.data.model.NData


@Database(entities = [NData::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun getDao(): NewsDao
}