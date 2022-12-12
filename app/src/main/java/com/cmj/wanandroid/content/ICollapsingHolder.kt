package com.cmj.wanandroid.content

import android.view.View

interface ICollapsingHolder {
    fun getCollapsingContainer(): View?
    fun addCollapsingView(view: View): Boolean
    fun setCollapsingView(view: View): Boolean
    fun clearCollapsingView(): Boolean
    fun setCollapsingExpanded(expanded: Boolean, animate: Boolean)
}