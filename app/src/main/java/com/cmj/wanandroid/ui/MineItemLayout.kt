package com.cmj.wanandroid.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.cmj.wanandroid.R

class MineItemLayout
@JvmOverloads
constructor(
    context: Context,
    attrSet: AttributeSet? = null,
    defAttrs: Int = 0,
    defRes: Int = 0
) : FrameLayout(context, attrSet, defAttrs, defRes) {

    private var icon: ImageView
    private var text: TextView
    private var enter: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.mine_item_layout, this, true)
        icon = findViewById(R.id.icon)
        text = findViewById(R.id.text)
        enter = findViewById(R.id.enter)
        val types = context.obtainStyledAttributes(attrSet, R.styleable.MineItemLayout, defAttrs, defRes)
        val itemIcon = types.getDrawable(R.styleable.MineItemLayout_mineItemIcon)
        val itemText = types.getText(R.styleable.MineItemLayout_mineItemText)
        val showEnter = types.getBoolean(R.styleable.MineItemLayout_mineItemEnterShow, true)
        if (itemIcon != null) icon.setImageDrawable(itemIcon)
        text.text = itemText
        enter.isVisible = showEnter
        types.recycle()
    }
}