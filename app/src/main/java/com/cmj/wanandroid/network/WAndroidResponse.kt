package com.cmj.wanandroid.network

//如果 retrofit 通过正常的 Call 作为返回值调用的话，会导致不管成功还是失败，都会回调 onResponse
open class WAndroidResponse<T>(
    open val data: T? = null,
    open val errorCode: Int = 0,
    open val errorMsg: String = ""
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
        else -> data!!
    }

    override fun toString(): String {
        return "WAndroidResponse(data=$data, errorCode=$errorCode, errorMsg='$errorMsg')"
    }

    class ServiceException(val errorCode: Int, private val errorMsg: String) : RuntimeException(errorMsg)
}