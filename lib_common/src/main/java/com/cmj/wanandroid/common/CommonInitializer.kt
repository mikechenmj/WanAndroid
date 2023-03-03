package com.cmj.wanandroid.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import com.tencent.mmkv.MMKV


class CommonInitializer : Initializer<Unit> {

    companion object {
        private val TAG = CommonInitializer::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private lateinit var globalContext: Context

        fun getContext() = globalContext
    }

    override fun create(context: Context) {
        globalContext = context
        MMKV.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}