package com.cmj.wanandroid.lib.network.factory

import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse.Error
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse.Ok
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse.ServiceException
import retrofit2.HttpException
import retrofit2.Response

// 支持 Call<WAndroidResponse<*>> 类型的返回值
class WASafeFactory : AbsSafeCallAdapterFactory<WAndroidResponse<*>>(WAndroidResponse::class.java) {
    override fun onResponse(response: Response<WAndroidResponse<*>>): WAndroidResponse<*> {
        return try {
            if (response.isSuccessful) {
                val rawBody = response.body()!!
                if (rawBody.code == WAndroidResponse.CODE_SUCCESS) {
                    Ok(rawBody.data)
                } else {
                    Error<Any>(ServiceException(rawBody.code, rawBody.msg))
                }
            } else {
                val throwable = HttpException(response)
                Error<Any>(throwable)
            }
        } catch (e: Exception) {
            Error<Any>(e)
        }

    }

    override fun onFailure(t: Throwable): WAndroidResponse<*> {
        return Error<Any>(t)
    }
}