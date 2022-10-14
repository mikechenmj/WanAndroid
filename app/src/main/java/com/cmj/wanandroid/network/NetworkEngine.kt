package com.cmj.wanandroid.network

import android.util.Log
import com.cmj.wanandroid.App
import com.cmj.wanandroid.network.test.TestApi
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkEngine {

    private val TAG = NetworkEngine::class.java.simpleName
    private const val DEFAULT_TIMEOUT = 30L
    const val BASE_URL = "https://www.wanandroid.com"


    private val cookieJar = WanAndroidCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(App.get()))

    val isLoggerIn : Boolean
        get() {
            return cookieJar.isLoggedIn()
        }

    val okhttp = createOkhttpClient()

    private val apiBuilder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            // 通过操作符对 Retrofit 操作结果进行异常捕获和 Bean 转换， 参考 Retrofit.kt，因此不使用以下 CallAdapterFactory
//            .addCallAdapterFactory(LiveDataResponseCallAdapterFactory())
//            .addCallAdapterFactory(LiveDataCallAdapterFactory())
//            .addCallAdapterFactory(FlowResponseCallAdapterFactory())
//            .addCallAdapterFactory(FlowCallAdapterFactory())
//            .addCallAdapterFactory(WABodySafeResultFactory())
//            .addCallAdapterFactory(WASafeResultFactory())
//            .addCallAdapterFactory(WASafeFactory())
            .client(okhttp)
            .build()
    }

    val testApi by lazy {
        apiBuilder.create(TestApi::class.java)
    }

    val userApi by lazy {
        apiBuilder.create(UserApi::class.java)
    }

    private fun createOkhttpClient(): OkHttpClient {
        val maxSize = 1024 * 1024
        return OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .cache(Cache(App.get().cacheDir, maxSize.toLong()))
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
            .build()
    }
}