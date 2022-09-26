package com.cmj.wanandroid.network

import android.util.Log
import com.cmj.wanandroid.network.kt.check
import okhttp3.Request
import okio.Timeout
import retrofit2.*
import java.lang.Exception
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 将请求结果转为 Result<T>，包括网络错误、服务器错误以及转换错误等都会被封装
 * 需注意的是，当返回值为 call 且调用 enqueue 时， onFailure 不会被调用，所有结果都应该从 onResponse 中接收。
 *
 * 只有当返回类型为 Call<Result<T>> 时才会生效， suspend 方法返回类型为 Result<T>
 *
 * R: response 的泛型类型
 * T: 返回的 Result 的泛型类型
 * targetConvertType: 指定的ConverterFactory转换类型，如果为空则默认从方法的返回类型解析，主要是为了Response 返回的 bend
 * 的 Converter 类型与方法的返回值的 bean 类型不同的场景。
 *
 */
abstract class AbsSafeResultCallAdapterFactory<R, T>() : CallAdapter.Factory() {

    protected class WrapDataType(
        private val wrapType: Type,
        private val dataType: Type
    ) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> = arrayOf(dataType)
        override fun getRawType(): Type = wrapType
        override fun getOwnerType(): Type? = null
    }

    abstract fun onResponse(response: Response<R>): Result<T>

    abstract fun onFailure(t: Throwable): Result<T>

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<R, Call<Result<T>>>? {
        if (SkipSafeCallAdapter::class.java.check(annotations)) return null
        if (getRawType(returnType) != Call::class.java) return null
        if (returnType !is ParameterizedType) return null

        val resultType: Type = getParameterUpperBound(0, returnType)
        if (getRawType(resultType) != Result::class.java
            || resultType !is ParameterizedType
        ) return null

        val dataType = getParameterUpperBound(0, resultType)
        val responseType = checkResultParameterUpperBound(dataType) ?: return null
        return SafeResultCallAdapter(responseType)
    }

    protected open fun checkResultParameterUpperBound(type: Type): Type? {
        return type
    }

    inner class SafeResultCallAdapter(
        private val responseType: Type,
    ) : CallAdapter<R, Call<Result<T>>> {
        override fun responseType(): Type = responseType
        override fun adapt(call: Call<R>): Call<Result<T>> = SafeResultCall(call)
    }

    inner class SafeResultCall(
        private val delegate: Call<R>,
    ) : Call<Result<T>> {

        override fun enqueue(callback: Callback<Result<T>>) {
            delegate.enqueue(object : Callback<R> {
                override fun onResponse(call: Call<R>, response: Response<R>) {
                    callback.onResponse(this@SafeResultCall, Response.success(onResponse(response)))
                }

                override fun onFailure(call: Call<R>, t: Throwable) {
                    callback.onResponse(this@SafeResultCall, Response.success(onFailure(t)))
                }
            })
        }

        override fun clone(): Call<Result<T>> = SafeResultCall(delegate)
        override fun execute(): Response<Result<T>> {
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