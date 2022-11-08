package com.cmj.wanandroid.network.api

import com.cmj.wanandroid.network.bean.User
import com.cmj.wanandroid.network.bean.WAndroidResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @POST("/user/login")
    @FormUrlEncoded
    fun login(@Field("username") userName: String, @Field("password") password: String): Call<WAndroidResponse<User>>

    @POST("/user/register")
    @FormUrlEncoded
    fun register(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): Call<WAndroidResponse<User>>

    @GET("/user/logout/json")
    fun logout(): Call<WAndroidResponse<Any?>>

}