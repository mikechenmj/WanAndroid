package com.cmj.wanandroid.network

import android.util.Log
import com.cmj.wanandroid.App
import com.cmj.wanandroid.BuildConfig
import com.cmj.wanandroid.base.log.WALog
import okhttp3.Cache
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetworkEngine {

    private const val DEFAULT_TIMEOUT = 30000L

    private val okhttp = createOkhttpClient()

    private val apiBuilder by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .addConverterFactory(SafeConverterFactory(ScalarsConverterFactory.create()))
            .addConverterFactory(SafeConverterFactory(GsonConverterFactory.create()))
            .addCallAdapterFactory(LiveDataResponseCallAdapterFactory())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addCallAdapterFactory(FlowResponseCallAdapterFactory())
            .addCallAdapterFactory(FlowCallAdapterFactory())
            .client(okhttp)
            .build()
    }

    val userApi by lazy {
        apiBuilder.create(UserApi::class.java)
    }

    private var cookies = emptyList<Cookie>()

    private fun createOkhttpClient(): OkHttpClient {
        val maxSize = 1024 * 1024
        return OkHttpClient.Builder()
//            .cookieJar(object : CookieJar {
//                override fun loadForRequest(url: HttpUrl): List<Cookie> {
//                    cookies.forEach {
//                        Log.i("MCJ", "loadForRequest cookie: $it")
//                    }
//                    return cookies
//                }
//
//                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
//                    cookies.forEach {
//                        Log.i("MCJ", "saveFromResponse cookie: $it")
//                    }
//                   this@NetworkEngine.cookies = cookies
//                }
//            })
            .cache(Cache(App.get().cacheDir, maxSize.toLong()))
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().apply { level = BODY })
            .build()
    }

    class SafeConverterFactory(implFactory: Converter.Factory) : DecorConverterFactory(
        implFactory,
        {
            try {
                it.invoke()
            } catch (e: Exception) {
                WALog.e("SafeConverterFactory", "response Converter failed: ${e.message}")
                if (BuildConfig.DEBUG) {
                    throw e
                }
                null
            }
        })
}