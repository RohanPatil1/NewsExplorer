package com.rohan.newsexplorer.data.model

import com.google.gson.annotations.SerializedName

data class NewsData(
    val category: String,
    @SerializedName("data")   val newDataList: List<NData>,
    val success: Boolean
)