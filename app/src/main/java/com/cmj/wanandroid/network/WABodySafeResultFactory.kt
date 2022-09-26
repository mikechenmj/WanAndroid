package com.cmj.wanandroid.network

import com.cmj.wanandroid.network.WAndroidResponse.ServiceException
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type

// 支持 Call<Result<*>> 类型的返回值
class WABodySafeResultFactory : AbsSafeResultCallAdapterFactory<WAndroidResponse<*>, Any?>() {

    override fun onResponse(response: Response<WAndroidResponse<*>>): Result<*> {
        return try {
            if (response.isSuccessful) {
                val body = response.body()!!
                if (body.errorCode == WAndroidResponse.CODE_SUCCESS) {
                    Result.success(body.getOrThrow())
                } else {
                    Result.failure(ServiceException(body.errorCode, body.errorMsg))
                }
            } else {
                val throwable = HttpException(response)
                Result.failure(throwable)
            }
        } catch (e: Exception) {
            Result.failure<Any?>(e)
        }
    }

    override fun onFailure(t: Throwable): Result<Any> {
        return Result.failure(t)
    }
    override fun checkResultParameterUpperBound(type: Type): Type? {
        if (getRawType(type) == WAndroidResponse::class.java) {
            return null
        }
        return WrapDataType(WAndroidResponse::class.java, type)
    }
}