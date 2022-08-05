package com.rohan.newsexplorer.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rohan.newsexplorer.data.model.NData


@Dao
interface NewsDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(newsDataList: List<NData>)

    @Insert(onConflict = REPLACE, entity = NData::class)
    suspend fun insert(nData: NData)

    @Query("SELECT * FROM newsTable")
    fun getNewsList(): List<NData>?

    @Delete
    fun deleteNews(nData: NData)

    @Delete
    fun deleteAllNews(newsDataList: List<NData>)

}