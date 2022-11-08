package com.cmj.wanandroid.network.factory

import com.cmj.wanandroid.network.bean.WAndroidResponse
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

// 支持 Call<Result<WAndroidResponse<*>> 类型的返回值
class WASafeResultFactory : AbsSafeResultCallAdapterFactory<WAndroidResponse<*>, WAndroidResponse<*>>() {

    override fun onResponse(response: Response<WAndroidResponse<*>>): Result<WAndroidResponse<*>> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()!!
                Result.success(body)
            } else {
                val throwable = HttpException(response)
                Result.failure(throwable)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun onFailure(t: Throwable): Result<WAndroidResponse<*>> {
        return Result.failure(t)
    }

    override fun checkResultParameterUpperBound(type: Type): Type? {
        if (getRawType(type) == WAndroidResponse::class.java) {
            return type
        }
        return null
    }
}
