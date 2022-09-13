package com.cmj.wanandroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App: Application() {

    companion object {

        private const val TAG = "APP"

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: App
        fun get() = context
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = this
    }

    override fun onCreate() {
        super.onCreate()
    }
}