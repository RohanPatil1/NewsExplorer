package com.rohan.newsexplorer.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Entity(tableName = "newsTable")
@Parcelize
data class NData(
    val author: String,
    val content: String,
    val date: String,
    val id: String,
    val imageUrl: String,
    val readMoreUrl: String?,
    val time: String,
    val title: String,
    val url: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @IgnoredOnParcel
    var nId: Int = 0
}