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

    data class ServiceError<T>(override val errorCode: Int, override val errorMsg: String) : WAndroidResponse<T>()

    data class ThrowableError<T>(val throwable: Throwable) : WAndroidResponse<T>()

    fun getOrNull(): T? = when (this) {
        is Ok -> data
        is ServiceError, is ThrowableError -> null
        else -> data
    }

    fun getOrThrow(): T = when (this) {
        is Ok -> data
        is ServiceError -> throw ServiceException(errorCode, errorMsg)
        is ThrowableError -> throw throwable
        else -> data!!
    }

    class ServiceException(val errorCode: Int, private val errorMsg: String) : RuntimeException(errorMsg)
}