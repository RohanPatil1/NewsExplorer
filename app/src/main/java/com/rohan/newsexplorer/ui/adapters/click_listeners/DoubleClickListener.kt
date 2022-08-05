package com.rohan.newsexplorer.ui.adapters.click_listeners

import android.view.View

abstract class DoubleClickListener : View.OnClickListener {
    var lastClickTime: Long = 0
    var tap = true
    override fun onClick(v: View) {
        val clickTime = System.currentTimeMillis()
        tap = if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
            onDoubleClick(v)
            false
        } else true
        v.postDelayed({ if (tap) onSingleClick() }, DOUBLE_CLICK_TIME_DELTA)
        lastClickTime = clickTime
    }

    abstract fun onDoubleClick(v: View?)
    abstract fun onSingleClick()

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 //milliseconds
    }
}