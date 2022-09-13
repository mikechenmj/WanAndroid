package com.cmj.wanandroid.network

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

class FlowResponseCallAdapterFactory : Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val returnRawType = getRawType(returnType)
        if (returnRawType != Flow::class.java) {
            return null
        }
        val flowParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(flowParameterizedType) != Response::class.java) {
            return null
        }
        val responseParameterizedType = getParameterUpperBound(0, flowParameterizedType as ParameterizedType)
        return FlowResponseAdapter(responseParameterizedType)
    }

    class FlowResponseAdapter(private val responseType: Type) : CallAdapter<Any, Flow<*>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<Any>): Flow<*> {
            return flow {
                try {
                    emit(suspendCancellableCoroutine {
                        call.enqueue(object : Callback<Any?> {
                            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                                it.resume(response, null)
                            }

                            override fun onFailure(call: Call<Any?>, t: Throwable) {
                                it.resume(null, null)
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
