package com.cmj.wanandroid.lib.network.bean

import com.squareup.moshi.Json

open class WAndroidResponse<out T> constructor(
    open val data: T? = null,
    @Json(name = "errorCode") open val code: Int = CODE_FAILED,
    @Json(name = "errorMsg") open val msg: String = ""
) {

    companion object {
        const val CODE_SUCCESS = 0
        const val CODE_FAILED = -1
        const val CODE_UN_LOGGED_IN = -1001
    }

    data class Ok<T>(override val data: T) : WAndroidResponse<T>()

    data class Error<T>(val throwable: Throwable) : WAndroidResponse<T>()

    fun getOrNull(): T? = when (this) {
        is Ok -> data
        is Error -> null
        else -> data
    }

    fun getOrThrow(): T = when (this) {
        is Ok -> data
        is Error -> throw throwable
        else -> {
            if (code == CODE_SUCCESS) {
                data as T
            } else {
                throw ServiceException(code, msg)
            }
        }
    }

    override fun toString(): String {
        return "WAndroidResponse(data=$data, errorCode=$code, errorMsg='$msg')"
    }

    class ServiceException(val errorCode: Int, private val errorMsg: String,  val url: String? = null) : RuntimeException(errorMsg)
}