package com.cmj.wanandroid.lib.network

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer

class NetworkInitializer : Initializer<Unit> {

    companion object {
        private const val TAG = "NetworkInitializer"

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