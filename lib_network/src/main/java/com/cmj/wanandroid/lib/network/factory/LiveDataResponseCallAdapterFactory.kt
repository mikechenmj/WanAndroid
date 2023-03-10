package com.cmj.wanandroid.lib.network.factory

import androidx.lifecycle.LiveData
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Callback
import retrofit2.Response

class LiveDataResponseCallAdapterFactory : Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any, LiveData<Response<Any>>>? {
        val returnRawType = getRawType(returnType)
        if (returnRawType != LiveData::class.java) {
            return null
        }
        val liveDataParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (getRawType(liveDataParameterizedType) != Response::class.java) {
            return null
        }
        val responseParameterizedType = getParameterUpperBound(0, liveDataParameterizedType as ParameterizedType)
        return LiveDataResponseCallAdapter(responseParameterizedType)
    }

    class LiveDataResponseCallAdapter(private val responseType: Type) : CallAdapter<Any, LiveData<Response<Any>>> {

        override fun responseType() = responseType //会影响 converter responseBodyConverter 方法传入的 type

        override fun adapt(call: Call<Any>): LiveData<Response<Any>> {
            return object : LiveData<Response<Any>>() {
                override fun onActive() {
                    super.onActive()
                    call.enqueue(object : Callback<Any> {
                        override fun onResponse(call: Call<Any>, response: Response<Any>) {
                            postValue(response)
                        }

                        override fun onFailure(call: Call<Any>, t: Throwable) {
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
