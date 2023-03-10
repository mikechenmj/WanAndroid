package com.cmj.wanandroid.lib.network.factory

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resumeWithException

class FlowCallAdapterFactory : Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any, Flow<Any>>? {
        val returnRawType = getRawType(returnType)
        if (returnRawType != Flow::class.java) {
            return null
        }
        val flowParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        return FlowCallAdapter(flowParameterizedType)
    }

    class FlowCallAdapter(private val responseType: Type) : CallAdapter<Any, Flow<Any>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<Any>): Flow<Any> {
            return flow {
                try {
                    emit(suspendCancellableCoroutine {
                        call.enqueue(object : Callback<Any> {
                            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                                val body = response.body()!!
                                it.resume(body, null)
                            }

                            override fun onFailure(call: Call<Any>, t: Throwable) {
                                it.resumeWithException(t)
                            }
                        })
                    })
                } catch (e: CancellationException) {
                    call.cancel()
                }
            }
        }
    }
}
