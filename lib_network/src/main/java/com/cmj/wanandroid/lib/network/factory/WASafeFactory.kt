package com.cmj.wanandroid.lib.network.factory

import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import retrofit2.HttpException
import retrofit2.Response

// 支持 Call<WAndroidResponse<*>> 类型的返回值
class WASafeFactory : AbsSafeCallAdapterFactory<WAndroidResponse<*>>(
    WAndroidResponse::class.java
) {
    override fun onResponse(response: Response<WAndroidResponse<*>>): WAndroidResponse<*> {
        return try {
            if (response.isSuccessful) {
                val rawBody = response.body()!!
                if (rawBody.code == WAndroidResponse.CODE_SUCCESS) {
                    WAndroidResponse.Ok(rawBody.data)
                } else {
                    WAndroidResponse.Error<Any>(
                        WAndroidResponse.ServiceException(
                            rawBody.code,
                            rawBody.msg
                        )
                    )
                }
            } else {
                val throwable = HttpException(response)
                WAndroidResponse.Error<Any>(throwable)
            }
        } catch (e: Exception) {
            WAndroidResponse.Error<Any>(e)
        }

    }

    override fun onFailure(t: Throwable): WAndroidResponse<*> {
        return WAndroidResponse.Error<Any>(t)
    }
}