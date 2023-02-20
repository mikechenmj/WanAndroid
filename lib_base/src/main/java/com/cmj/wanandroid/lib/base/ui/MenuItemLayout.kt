package com.cmj.wanandroid.lib.base.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.cmj.wanandroid.lib.base.R

class MenuItemLayout
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
        LayoutInflater.from(context).inflate(R.layout.menu_item_layout, this, true)
        icon = findViewById(R.id.icon)
        text = findViewById(R.id.text)
        enter = findViewById(R.id.enter)
        val types = context.obtainStyledAttributes(attrSet, R.styleable.MenuItemLayout, defAttrs, defRes)
        val itemIcon = types.getDrawable(R.styleable.MenuItemLayout_menuItemIcon)
        val itemText = types.getText(R.styleable.MenuItemLayout_menuItemText)
        val showEnter = types.getBoolean(R.styleable.MenuItemLayout_menuItemEnterShow, true)
        if (itemIcon != null) icon.setImageDrawable(itemIcon)
        text.text = itemText
        enter.isVisible = showEnter
        types.recycle()
    }
}