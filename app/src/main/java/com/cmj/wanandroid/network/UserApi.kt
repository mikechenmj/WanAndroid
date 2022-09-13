package com.cmj.wanandroid.network

import androidx.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun login(@Field("username") userName: String, @Field("password") password: String): Response<String>

    @POST("/user/register")
    @FormUrlEncoded
    suspend fun register(@Field("username") userName: String,
                         @Field("password") password: String,
                         @Field("repassword") rePassword: String): Response<String>

    @GET("/user/logout/json")
    suspend fun logout(): Response<String>
}