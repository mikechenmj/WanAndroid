package com.cmj.wanandroid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.FrameLayout

class HorizontalInterceptorView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private var tracker: VelocityTracker? = null
    private val flingVelocity = ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            tracker?.recycle()
            tracker = VelocityTracker.obtain()
            tracker!!.addMovement(ev)
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            tracker!!.addMovement(ev)
            tracker!!.computeCurrentVelocity(1000)
            val xVelocity: Float = Math.abs(tracker!!.xVelocity)
            val yVelocity: Float = Math.abs(tracker!!.yVelocity)
            if (xVelocity > flingVelocity || yVelocity > flingVelocity) {
                if (xVelocity >= yVelocity * 1.5) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}