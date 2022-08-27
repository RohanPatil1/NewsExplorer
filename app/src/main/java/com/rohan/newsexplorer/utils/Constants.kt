package com.rohan.newsexplorer.utils

object Constants {
    const val BASE_URL = "https://inshorts.deta.dev/"
    const val HOME = 0
    const val DOWNLOADS = 1
    const val NOTIFICATION_ALARM_REQ_ID = 12
    const val DAILY_NEWS_NOTIFICATION_ID = 1
    const val DAILY_8AM_NOTIFICATION = "DAILY_8AM_NOTIFICATION"
    const val DEFAULT_CATEGORY = "world"
    const val DEFAULT_DISCOVER_CATEGORY = "all"
    const val TIP_KEY = "TipSharedPref"
    const val SHOULD_DISABLE_TIP = "shouldDisableTip"
    const val CAN_SHOW_ONLINE = "canShowOnline"
    const val IS_FRESH_START = "isFreshStart"
    const val SHARED_PREFS = "sharedPrefs"
    val categories = listOf<String>(
        "all",
        "national",
        "business",
        "sports",
        "world",
        "politics",
        "technology",
        "startup",
        "entertainment",
        "miscellaneous",
        "hatke",
        "science",
        "automobile"
    )

}