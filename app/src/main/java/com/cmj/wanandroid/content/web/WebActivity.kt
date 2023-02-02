package com.cmj.wanandroid.content.web

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel

class WebActivity: AbsWebActivity<ViewModel>() {
    companion object {

        fun start(context: Context, url: String) {
            context.startActivity(Intent(context, WebActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            })
        }

        fun start(context: Context, intent: Intent, url: String) {
            context.startActivity(intent.apply {
                setClassName(context.packageName, WebActivity::class.java.name)
                putExtra(EXTRA_URL, url)
            })
        }
    }
}