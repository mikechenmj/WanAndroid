package com.cmj.wanandroid.kt

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cmj.wanandroid.BuildConfig
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.network.bean.WAndroidResponse.Companion.CODE_UN_LOGGED_IN
import com.cmj.wanandroid.network.bean.WAndroidResponse.ServiceException
import com.cmj.wanandroid.ui.AppToast
import com.cmj.wanandroid.user.UserActivity
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

private fun toastError(context: Context, e: Throwable, errMsg: String? = null, duration: Int = Toast.LENGTH_SHORT) {
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


fun <T> Result<T>.getOrHandleError(context: Context): T? {
    onFailure {
        handleIfError(context)
        return null
    }
    return getOrNull()
}

fun <T> Result<T>.handleIfError(context: Context): Boolean {
    val exception = exceptionOrNull() ?: return false
    return handleError(context, exception)
}

fun handleError(context: Context, exception: Throwable): Boolean {
    if (exception is ServiceException) {
        if (exception.errorCode == CODE_UN_LOGGED_IN) {
            context.startActivity(Intent(context, UserActivity::class.java))
        }
    }
    toastError(context, exception)
    return true
}