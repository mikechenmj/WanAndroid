package com.cmj.wanandroid.kt

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.cmj.wanandroid.common.AppToast
import com.cmj.wanandroid.common.log.LogMan
import com.cmj.wanandroid.lib.network.BuildConfig
import com.cmj.wanandroid.lib.network.R
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse.Companion.CODE_UN_LOGGED_IN
import com.cmj.wanandroid.lib.network.kt.toastError
import com.cmj.wanandroid.user.UserActivity
import java.net.UnknownHostException

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
            context.startActivity(Intent(context, UserActivity::class.java))
        }
    }
    toastError(context, exception)
    return true
}