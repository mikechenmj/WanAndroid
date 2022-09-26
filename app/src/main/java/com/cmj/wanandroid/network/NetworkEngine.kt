package com.cmj.wanandroid.network

import com.cmj.wanandroid.App
import com.cmj.wanandroid.network.test.TestApi
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
    private const val DEFAULT_TIMEOUT = 30000L
    const val BASE_URL = "https://www.wanandroid.com"

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
//            .addCallAdapterFactory(LiveDataResponseCallAdapterFactory())
//            .addCallAdapterFactory(LiveDataCallAdapterFactory())
//            .addCallAdapterFactory(FlowResponseCallAdapterFactory())
//            .addCallAdapterFactory(FlowCallAdapterFactory())
            .addCallAdapterFactory(WABodySafeResultFactory())
            .addCallAdapterFactory(WASafeResultFactory())
            .addCallAdapterFactory(WASafeFactory())
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
            .cookieJar(WanAndroidCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(App.get())))
            .cache(Cache(App.get().cacheDir, maxSize.toLong()))
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
            .build()
    }
}