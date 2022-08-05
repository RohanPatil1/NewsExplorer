package com.rohan.newsexplorer.ui.view_models

import android.graphics.Color
import com.rohan.newsexplorer.R


data class Category(
    val title: String,
    val endPoint: String,
    val icon: Int,
    val color1: Int,
    val color2: Int
)

object CategoryItemUtils {
    val categoryList = listOf(
        Category(
            "All",
            "all",
            R.drawable.ic_explore,
            Color.parseColor("#4776E6"),
            Color.parseColor("#8E54E9")
        ),
        Category(
            "Popular", "hatke",
            R.drawable.ic_hatke,
            Color.parseColor("#FF1010"),
            Color.parseColor("#FFDC5D")
        ),
        Category(
            "Sports", "sports",
            R.drawable.ic_sports,
            Color.parseColor("#EF32D9"),
            Color.parseColor("#FA7AD2")
        ),
        Category(
            "Business", "business",
            R.drawable.ic_business,
            Color.parseColor("#000DFF"),
            Color.parseColor("#6B73FF")
        ),
        Category(
            "World", "world",
            R.drawable.ic_globe,
            Color.parseColor("#56ab2f"),
            Color.parseColor("#a8e063")
        ),
        Category(
            "Politics", "politics",
            R.drawable.ic_flag,
            Color.parseColor("#F56A14"),
            Color.parseColor("#FAAB7A")
        ),
        Category(
            "Technology", "technology",
            R.drawable.ic_tech,
            Color.parseColor("#087584"),
            Color.parseColor("#24C6DC")
        ),

        Category(
            "Startup", "startup",
            R.drawable.ic_startup,
            Color.parseColor("#FF1010"),
            Color.parseColor("#6A5AFF")
        ),
        Category(
            "India", "national",
            R.drawable.ic_inr,
            Color.parseColor("#FF9933"),
            Color.parseColor("#138808")
        ),
        Category(
            "Entertainment", "entertainment",
            R.drawable.ic_entertainment,
            Color.parseColor("#7F00FF"),
            Color.parseColor("#E100FF")
        ),
        Category(
            "Miscellaneous", "miscellaneous",
            R.drawable.ic_misc,
            Color.parseColor("#dd5e89"),
            Color.parseColor("#f7bb97")
        ),
        Category(
            "Science", "science",
            R.drawable.ic_science,
            Color.parseColor("#5C258D"),
            Color.parseColor("#6A5AFF")
        ),
        Category(
            "Automobile", "automobile",
            R.drawable.ic_automobile,
            Color.parseColor("#000DFF"),
            Color.parseColor("#6B73FF")
        ),
    )
}