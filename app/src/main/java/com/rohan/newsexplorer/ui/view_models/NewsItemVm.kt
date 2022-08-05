package com.rohan.newsexplorer.ui.view_models

import com.rohan.newsexplorer.data.model.NData

class NewsItemVm(
    private val tvItem: NData
) {
    val title = tvItem.title
    val content = tvItem.content
    val author = tvItem.author
    val img = tvItem.imageUrl
    val time = tvItem.time
    val date = tvItem.date
    val readMoreUrl = tvItem.readMoreUrl
}