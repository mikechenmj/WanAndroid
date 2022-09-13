package com.cmj.wanandroid.network

import android.util.Log
import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory : Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val returnRawType = getRawType(returnType)
        if (returnRawType != LiveData::class.java) {
            return null
        }
        val liveDataParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawLiveDataParameterizedType = getRawType(liveDataParameterizedType)
        return LiveDataCallAdapter(rawLiveDataParameterizedType)
    }

    class LiveDataCallAdapter(private val responseType: Type) : CallAdapter<Any, LiveData<*>> {

        override fun responseType() = responseType //会影响 converter responseBodyConverter 方法传入的 type

        override fun adapt(call: Call<Any>): LiveData<*> {
            return object : LiveData<Any>() {
                override fun onActive() {
                    super.onActive()
                    call.enqueue(object : Callback<Any?> {

                        override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                            val body = response.body()
                            postValue(body)
                        }

                        override fun onFailure(call: Call<Any?>, t: Throwable) {
                            postValue(null)
                        }
                    })
                }

                override fun onInactive() {
                    call.cancel()
                }
            }
        }
    }
}
