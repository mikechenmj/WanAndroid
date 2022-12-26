package com.cmj.wanandroid.ui

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.viewpager2.widget.ViewPager2.PageTransformer
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class RingPageTransformer : PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val absPosition = abs(position)
        if (absPosition in 0.0..1.0) {
            page.translationX = -1 * page.width * position // 抵消默认动画
            page.translationZ = 0f
            page.clipToOutline = true
            page.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    view ?: return
                    outline ?: return
                    val max = sqrt((view.width.toDouble() / 2).pow(2) + (view.height.toDouble() / 2f).pow(2)).toInt() + 10
                    val offset = (max * (1 - position)).toInt()
                    val left = view.width / 2 - offset
                    val top = view.height / 2 - offset
                    val right = view.width - left
                    val bottom = view.height - top
                    outline.setOval(left, top, right, bottom)
                }
            }
        } else {
            page.translationZ = -absPosition
        }
    }
}