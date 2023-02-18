package com.cmj.wanandroid.lib.base.kt

import android.content.Context
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse.Companion.CODE_UN_LOGGED_IN
import com.cmj.wanandroid.lib.network.kt.toastError

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
    if (exception is WAndroidResponse.ServiceException) {
        if (exception.errorCode == CODE_UN_LOGGED_IN) {
            // to do
//            context.startActivity(Intent(context, UserActivity::class.java))
        }
    }
    toastError(context, exception)
    return true
}