package com.cmj.wanandroid.lib.network.kt

import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import com.cmj.wanandroid.lib.network.factory.SkipSafeCallAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Invocation
import retrofit2.Response

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Request
import okio.Timeout
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.full.primaryConstructor

suspend fun <T : Any> Call<T>.suspendAwait(): T {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        val invocation = call.request().tag(Invocation::class.java)!!
                        val method = invocation.method()
                        val e = KotlinNullPointerException(
                            "Response from " +
                                    method.declaringClass.name +
                                    '.' +
                                    method.name +
                                    " was null but response body type was declared as non-null"
                        )
                        continuation.resumeWithException(e)
                    } else {
                        continuation.resume(body)
                    }
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

suspend fun <T : Any> Call<T?>.awaitNullable(): T? {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body())
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            }

            override fun onFailure(call: Call<T?>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

suspend fun <T> Call<T>.suspendAwaitResponse(): Response<T> {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(response)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

fun <T> Call<T>.flow(): Flow<T> {
    return flow {
        try {
            emit(suspendCancellableCoroutine {
                enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        val body = response.body()!!
                        it.resume(body, null)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        it.resumeWithException(t)
                    }
                })
            })
        } catch (e: CancellationException) {
            cancel()
        }
    }
}

fun <T> Call<T>.responseFlow(): Flow<Response<T>> {
    return flow {
        try {
            emit(suspendCancellableCoroutine {
                enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        it.resume(response, null)
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        it.resumeWithException(t)
                    }
                })
            })
        } catch (e: CancellationException) {
            cancel()
        }
    }
}

fun Class<SkipSafeCallAdapter>.check(annotations: Array<out Annotation>): Boolean {
    for (annotation in annotations) {
        if (this.isInstance(annotation)) {
            return true
        }
    }
    return false
}

fun <T> Call<T>.resultCall(): Call<Result<T>> {
    return SafeResultCall(this)
}

fun <T> Call<WAndroidResponse<T>>.resultWABodyCall(): Call<Result<T>> {
    return SafeResultWABodyCall(this)
}

fun <T> Call<WAndroidResponse<T>>.safeWACall(): Call<WAndroidResponse<T>> {
    return SafeWACall(this)
}

// ???????????????????????????????????? callback.onResponse ??????????????????????????????????????????????????????
abstract class AbsDelegateCall<T, R>(private val delegate: Call<T>) : Call<R> {

    protected abstract fun onSuccess(response: Response<T>): R
    protected abstract fun onFailure(t: Throwable): R

    override fun enqueue(callback: Callback<R>) {
        delegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(this@AbsDelegateCall, Response.success(onSuccess(response)))
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(this@AbsDelegateCall, Response.success(onFailure(t)))
            }
        })
    }

    override fun clone(): Call<R> = this::class.primaryConstructor!!.call(delegate)
    override fun execute(): Response<R> {
        return try {
            Response.success(onSuccess(delegate.execute()))
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

class SafeResultCall<T>(delegate: Call<T>) : AbsDelegateCall<T, Result<T>>(delegate) {

    override fun onSuccess(response: Response<T>): Result<T> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()!!
                Result.success(body)
            } else {
                val throwable = HttpException(response)
                Result.failureAndLogDebug(throwable)
            }
        } catch (e: Exception) {
            Result.failureAndLogDebug(e)
        }
    }

    override fun onFailure(t: Throwable): Result<T> {
        return Result.failureAndLogDebug(t)
    }
}

class SafeResultWABodyCall<T>(delegate: Call<WAndroidResponse<T>>) : AbsDelegateCall<WAndroidResponse<T>, Result<T>>(delegate) {

    override fun onSuccess(response: Response<WAndroidResponse<T>>): Result<T> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.code == WAndroidResponse.CODE_SUCCESS) {
                    Result.success(body.getOrThrow())
                } else {
                    Result.failureAndLogDebug(
                        WAndroidResponse.ServiceException(
                            body.code,
                            body.msg
                        )
                    )
                }
            } else {
                val throwable = HttpException(response)
                Result.failureAndLogDebug(throwable)
            }
        } catch (e: Exception) {
            Result.failureAndLogDebug(e)
        }
    }

    override fun onFailure(t: Throwable): Result<T> {
        return Result.failureAndLogDebug(t)
    }
}

class SafeWACall<T>(delegate: Call<WAndroidResponse<T>>) : AbsDelegateCall<WAndroidResponse<T>, WAndroidResponse<T>>(delegate) {

    override fun onSuccess(response: Response<WAndroidResponse<T>>): WAndroidResponse<T> {
        return try {
            if (response.isSuccessful) {
                val rawBody = response.body()!!
                if (rawBody.code == WAndroidResponse.CODE_SUCCESS) {
                    WAndroidResponse.Ok(rawBody.data!!)
                } else {
                    WAndroidResponse.Error(
                        WAndroidResponse.ServiceException(
                            rawBody.code,
                            rawBody.msg
                        )
                    )
                }
            } else {
                val throwable = HttpException(response)
                WAndroidResponse.Error(throwable)
            }
        } catch (e: Exception) {
            WAndroidResponse.Error(e)
        }
    }

    override fun onFailure(t: Throwable): WAndroidResponse<T> {
        return WAndroidResponse.Error(t)
    }
}