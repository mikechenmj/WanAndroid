package com.cmj.wanandroid.network

import com.cmj.wanandroid.network.kt.check
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 将请求结果转为 T，包括网络错误、服务器错误以及转换错误等都会被封装
 * 需注意的是，当返回值为 call 且调用 enqueue 时， onFailure 不会被调用，所有结果都应该从 onResponse 中接收。
 *
 * 只有当返回类型为 Call<T> 时才会生效， suspend 方法返回类型为 T
 * 只有当 T 与 targetResponseType 一致时才生效。
 */
abstract class AbsSafeCallAdapterFactory<T>(private val targetResponseType: Type) : CallAdapter.Factory() {

   abstract fun onResponse(response: Response<T>): T
    abstract fun onFailure(t: Throwable): T

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<T, Call<T>>? {
        if (SkipSafeCallAdapter::class.java.check(annotations)) return null
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType)

        val innerType: Type = getParameterUpperBound(0, returnType)
        if (getRawType(innerType) != targetResponseType) return null

        return SafeCallAdapter(
            innerType,
            retrofit
        )
    }

    inner class SafeCallAdapter(
        private val responseType: Type,
        val retrofit: Retrofit,
    ) : CallAdapter<T, Call<T>> {
        override fun responseType(): Type = responseType
        override fun adapt(call: Call<T>): Call<T> = SafeResponseCall(call)

    }

    inner class SafeResponseCall(
        private val delegate: Call<T>,
    ) : Call<T> {

        override fun enqueue(
            callback: Callback<T>
        ): Unit = delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@SafeResponseCall, Response.success(onResponse(response)))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@SafeResponseCall, Response.success(onFailure(t)))
            }
        })

        override fun clone(): Call<T> = SafeResponseCall(delegate)

        override fun execute(): Response<T> {
            return try {
                Response.success(onResponse(delegate.execute()))
            } catch (e: Exception) {
                Response.success(onFailure(e))
            }
        }

        override fun isExecuted(): Boolean = delegate.isExecuted
        override fun cancel(): Unit = delegate.cancel()
        override fun isCanceled(): Boolean = delegate.isCanceled
        override fun request(): Request = delegate.request()
        override fun timeout(): Timeout = delegate.timeout()
    }
}