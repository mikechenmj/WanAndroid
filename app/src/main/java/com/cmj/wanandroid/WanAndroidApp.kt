package com.cmj.wanandroid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.tencent.mmkv.MMKV

class WanAndroidApp: Application() {

    companion object {

        private const val TAG = "APP"

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: WanAndroidApp
        fun get() = context
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = this
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(context)
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(this)
    }
}