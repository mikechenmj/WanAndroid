package com.cmj.wanandroid.lib.base.ui

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Paint.Style.STROKE
import android.graphics.Path
import android.graphics.Path.Direction.CW
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.ConvertUtils
import com.cmj.wanandroid.common.CommonInitializer
import com.cmj.wanandroid.lib.base.R

class FillPrimaryCornerDrawable : CornerDrawable() {

    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = FILL
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color = CommonInitializer.getContext().getColor(R.color.color_primary)
        }
    }
}

class FillOnPrimaryCornerDrawable : CornerDrawable() {

    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = FILL
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color = CommonInitializer.getContext().getColor(R.color.color_on_primary)
        }
    }
}

class StrokeOnPrimaryCornerDrawable : CornerDrawable() {
    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = STROKE
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color = CommonInitializer.getContext().getColor(R.color.color_on_primary)
        }
    }
}

class StrokePrimaryCornerDrawable : CornerDrawable() {
    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = STROKE
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color = CommonInitializer.getContext().getColor(R.color.color_primary)
        }
    }
}

class StrokePrimaryDarkCornerDrawable : CornerDrawable() {
    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = STROKE
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color = CommonInitializer.getContext().getColor(R.color.color_primary_dark)
        }
    }
}

class StrokeRedTipDrawable : CornerDrawable() {
    override fun initPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = STROKE
            this.strokeWidth = ConvertUtils.dp2px(1f).toFloat()
            this.color =CommonInitializer.getContext().getColor(R.color.color_red_tip)
        }
    }
}

abstract class CornerDrawable : Drawable() {

    private val paint: Paint by lazy { initPaint() }

    abstract fun initPaint(): Paint

    override fun draw(canvas: Canvas) {
        val padding = paint.strokeWidth * 2
        val radius = bounds.height() / 2f
        val path = Path().apply {
            addRoundRect(
                padding, padding,
                bounds.width().toFloat() - padding,
                bounds.height().toFloat() - padding,
                radius, radius, CW
            )
        }
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}