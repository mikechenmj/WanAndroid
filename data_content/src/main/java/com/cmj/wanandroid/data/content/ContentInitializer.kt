package com.cmj.wanandroid.data.content

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

class ContentInitializer : Initializer<Unit> {

    companion object {
        private val TAG = ContentInitializer::class.java.simpleName
    }

    override fun create(context: Context) {
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}