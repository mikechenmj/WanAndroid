package com.cmj.wanandroid.network

import com.cmj.wanandroid.network.WAndroidResponse.ServiceException
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

// 支持 Call<Result<WAndroidResponse<*>> 类型的返回值
class WASafeResultFactory : AbsSafeResultCallAdapterFactory<WAndroidResponse<*>, WAndroidResponse<*>>() {

    override fun onResponse(response: Response<WAndroidResponse<*>>): Result<WAndroidResponse<*>> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.errorCode == WAndroidResponse.CODE_SUCCESS) {
                    Result.success(WAndroidResponse.Ok(body.getOrThrow()))
                } else {
                    Result.success(WAndroidResponse.Error<Any>(ServiceException(body.errorCode, body.errorMsg)))
                }
            } else {
                val throwable = HttpException(response)
                Result.success(WAndroidResponse.Error<Any>(throwable))
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
