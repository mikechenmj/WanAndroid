package com.cmj.wanandroid.common.log

import android.util.Log

object LogMan {

    private const val TAG = "WA_"

    private var logLevel = Log.VERBOSE

    fun setLogLevel(level: Int) {
        if (level < Log.VERBOSE || level > Log.ASSERT) {
            return
        }
        logLevel = level
    }

    fun getLogLevel(): Int {
        return logLevel
    }

    @JvmStatic
    @JvmOverloads
    fun v(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.VERBOSE) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.v("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    @JvmOverloads
    fun d(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.DEBUG) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.d("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    @JvmOverloads
    fun i(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.INFO) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.i("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    @JvmOverloads
    fun w(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.WARN) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.w("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.ERROR) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.e("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    @JvmOverloads
    fun wtf(tag: String, msg: String = "", tr: Throwable? = null) {
        if (logLevel > Log.ERROR) {
            return
        }
        val throwableStr = tr?.let {
            " throwable: ${it.message}"
        } ?: ""

        Log.wtf("$TAG$tag", msg + throwableStr)
    }

    @JvmStatic
    fun getStackTraceString(tr: Throwable) = Log.getStackTraceString(tr)
}