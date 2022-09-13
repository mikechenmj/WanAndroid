package com.cmj.wanandroid.network

import android.util.Log
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.Type

open class DecorConverterFactory(
    private val implFactory: Converter.Factory,
    private val responseDecorConvert: (() -> Any?) -> Any? = { it.invoke() },
    private val requestDecorConvert: (() -> RequestBody) -> RequestBody = { it.invoke() },
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val converter = implFactory.responseBodyConverter(type, annotations, retrofit) ?: return null
        return ResponseConverter(converter, responseDecorConvert)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val converter = implFactory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit) ?: return null
        return RequestConverter(converter, requestDecorConvert)
    }

    class ResponseConverter<T>(
        private val implConverter: Converter<ResponseBody, T>,
        private val decor: (() -> Any?) -> Any?
    ) : Converter<ResponseBody, T> {

        override fun convert(value: ResponseBody): T? {
            return decor {
                implConverter.convert(value)!!
            } as? T
        }
    }

    class RequestConverter<F>(
        private val implConverter: Converter<F, RequestBody>,
        private val decor: (() -> RequestBody) -> RequestBody
    ) : Converter<F, RequestBody> {

        override fun convert(value: F): RequestBody {
            return decor {
                implConverter.convert(value)!!
            }
        }
    }
}