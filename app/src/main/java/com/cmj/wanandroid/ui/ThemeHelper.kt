package com.cmj.wanandroid.ui

import android.view.ContextThemeWrapper
import com.cmj.wanandroid.R

fun ContextThemeWrapper.obtainStyledAttributesColor(attr: Int): Int {
    obtainStyledAttributes(intArrayOf(attr)).let {
        val value = it.getColor(0, 0)
        it.recycle()
        return value
    }
}

fun <T : ContextThemeWrapper> T.getColorPrimary() = obtainStyledAttributesColor(R.attr.colorPrimary)

fun <T : ContextThemeWrapper> T.getColorPrimaryDark() = obtainStyledAttributesColor(R.attr.colorPrimaryDark)

fun <T : ContextThemeWrapper> T.getColorOnPrimary() = obtainStyledAttributesColor(R.attr.colorOnPrimary)

fun <T : ContextThemeWrapper> T.getTextColorPrimary() = obtainStyledAttributesColor(android.R.attr.textColorPrimary)

fun <T : ContextThemeWrapper> T.getTextColorSecondary() = obtainStyledAttributesColor(R.attr.colorPrimaryVariant)

fun <T : ContextThemeWrapper> T.getStatusBarHeight() = resources.getDimension(resources.getIdentifier("status_bar_height", "dimen", "android"))

