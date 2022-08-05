package com.rohan.newsexplorer.ui.adapters.click_listeners

import com.rohan.newsexplorer.data.model.NData

interface NewsItemOnClick {

    fun onTitleDoubleTap(nData: NData)
    fun onReadMore(readMoreUrl: String)

}