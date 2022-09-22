package com.cmj.wanandroid.network

import android.util.Log
import com.cmj.wanandroid.App
import com.cmj.wanandroid.base.log.WALog
import com.cmj.wanandroid.network.SafeCallAdapterFactory.CallHandler
import com.cmj.wanandroid.network.SafeResultCallAdapterFactory.ResultCallHandler
import com.cmj.wanandroid.network.WAndroidResponse.ServiceException
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

object NetworkEngine {

    private val TAG = NetworkEngine::class.java.simpleName
    private const val DEFAULT_TIMEOUT = 30000L
    const val BASE_URL = "https://www.wanandroid.com"

    val okhttp = createOkhttpClient()

    // 支持 Call<WAndroidResponse<Any>> 类型的返回值，Any 为 WAndroidResponse 里的 data 类型
    private val wASafeCallAdapterFactory = SafeCallAdapterFactory(object : CallHandler<WAndroidResponse<*>> {
        override fun onResponse(response: Response<WAndroidResponse<*>>): WAndroidResponse<*> {
            try {
                return if (response.isSuccessful) {
                    val rawBody =
                        response.body()!!
                    if (rawBody.errorCode == WAndroidResponse.CODE_SUCCESS) {
                        WAndroidResponse.Ok(rawBody.data)
                    } else {
                        WAndroidResponse.ServiceError<Any>(rawBody.errorCode, rawBody.errorMsg)
                    }
                } else {
                    val throwable = HttpException(response)
                    WALog.w(TAG, "SafeCallAdapterFactory.onResponse error: $throwable")
                    WAndroidResponse.ThrowableError<Any>(throwable)
                }
            } catch (e: Exception) {
                return WAndroidResponse.ThrowableError<Any>(e)
            }

        }

        override fun onFailure(t: Throwable): WAndroidResponse<*> {
            WALog.w(TAG, "SafeCallAdapterFactory.onFailure: $t")
            return WAndroidResponse.ThrowableError<Any>(t)
        }
    }, WAndroidResponse::class.java)

    // 支持 Call<Result<Any>> 类型的返回值，Any 为 WAndroidResponse 里的 data 类型
    private val wASafeResultCallAdapterFactory = SafeResultCallAdapterFactory(object : ResultCallHandler<WAndroidResponse<Any>, Any> {
        override fun onResponse(response: Response<WAndroidResponse<Any>>): Result<Any> {
            try {
                return if (response.isSuccessful) {
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
                return Result.failure(e)
            }
        }

        override fun onFailure(t: Throwable): Result<Any> {
            return Result.failure(t)
        }
    }, WAndroidResponse::class.java)

    private val apiBuilder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .addCallAdapterFactory(LiveDataResponseCallAdapterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(FlowResponseCallAdapterFactory())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .addCallAdapterFactory(wASafeCallAdapterFactory)
            .addCallAdapterFactory(wASafeResultCallAdapterFactory)
            .client(okhttp)
            .build()
    }

    val userApi by lazy {
        apiBuilder.create(UserApi::class.java)
    }

    private fun createOkhttpClient(): OkHttpClient {
        val maxSize = 1024 * 1024
        return OkHttpClient.Builder()
            .cookieJar(WanAndroidCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(App.get())))
            .cache(Cache(App.get().cacheDir, maxSize.toLong()))
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
            .build()
    }
}