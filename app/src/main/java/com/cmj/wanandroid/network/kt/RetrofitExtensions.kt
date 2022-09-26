package com.cmj.wanandroid.network.kt

import com.cmj.wanandroid.network.SkipSafeCallAdapter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Invocation
import retrofit2.Response

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

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
                        val e = KotlinNullPointerException("Response from " +
                                method.declaringClass.name +
                                '.' +
                                method.name +
                                " was null but response body type was declared as non-null")
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

fun isAnnotationPresent(annotations: Array<Annotation?>, cls: Class<out Annotation?>): Boolean {
    for (annotation in annotations) {
        if (cls.isInstance(annotation)) {
            return true
        }
    }
    return false
}

fun Class<SkipSafeCallAdapter>.check(annotations: Array<out Annotation>): Boolean {
    for (annotation in annotations) {
        if (this.isInstance(annotation)) {
            return true
        }
    }
    return false
}