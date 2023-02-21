package com.cmj.wanandroid.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

class CommonInitializer : Initializer<Unit> {

    companion object {
        private val TAG = CommonInitializer::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private lateinit var globalContext: Context

        fun getContext() = globalContext
    }

    override fun create(context: Context) {
        globalContext = context
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}