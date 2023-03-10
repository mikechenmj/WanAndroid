package com.cmj.wanandroid.lib.network.kt

import android.content.Context
import android.widget.Toast
import com.cmj.wanandroid.common.AppToast
import com.cmj.wanandroid.common.log.LogMan
import com.cmj.wanandroid.lib.network.BuildConfig
import com.cmj.wanandroid.lib.network.R
import java.net.UnknownHostException

fun <T> Result<T>.getOrToastError(
    context: Context,
    duration: Int = Toast.LENGTH_SHORT,
    onErrorText: ((Exception) -> String?) = { null }
): T? {
    try {
        return getOrThrow()
    } catch (e: Exception) {
        var errMsg = onErrorText(e)
        toastError(context, e, errMsg, duration)
    }
    return null
}

fun toastError(
    context: Context,
    e: Throwable,
    errMsg: String? = null,
    duration: Int = Toast.LENGTH_SHORT
) {
    var msg = errMsg
    if (msg == null) {
        msg = when (e) {
            is UnknownHostException -> {
                context.getString(R.string.network_wrong_tip)
            }
            else -> {
                e.message ?: e.toString()
            }
        }
    }
    AppToast.toast(context, msg, duration)
}

fun <T> Result.Companion.failureAndLogDebug(exception: Throwable): Result<T> {
    LogMan.w("result failure: $exception")
    if (BuildConfig.DEBUG) {
        exception.printStackTrace()
    }
    return failure(exception)
}