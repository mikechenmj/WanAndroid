package com.cmj.wanandroid.test

import androidx.lifecycle.LiveData
import com.cmj.wanandroid.network.bean.LoginInfo
import com.cmj.wanandroid.network.bean.WAndroidResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TestApi {
    //------------------- WAndroidResponse
    @POST("/user/login")
    @FormUrlEncoded
    fun testWA(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Call<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWASuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): WAndroidResponse<LoginInfo>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWAResponseSuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Response<WAndroidResponse<LoginInfo>>

    //------------------- result WAndroidResponse
    @POST("/user/login")
    @FormUrlEncoded
    fun testWAResult(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Call<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWAResultResponseSuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Response<Result<WAndroidResponse<LoginInfo>>>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWAResultSuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Result<WAndroidResponse<LoginInfo>>

    //------------------- ResultBody
    @POST("/user/login")
    @FormUrlEncoded
    fun testWAResultBody(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Call<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWAResultBodySuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Result<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    suspend fun testWAResultBodyResponseSuspend(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Response<Result<WAndroidResponse<LoginInfo>>>

    //------------------- flow

    @POST("/user/login")
    @FormUrlEncoded
    fun testWAFlow(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Flow<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    fun testWAResponseFlow(
        @Field("username") userName: String,
        @Field("password") password: String
    ): Flow<Response<WAndroidResponse<LoginInfo>>>

    //------------------- liveData
    @POST("/user/login")
    @FormUrlEncoded
    fun testWALiveData(
        @Field("username") userName: String,
        @Field("password") password: String
    ): LiveData<WAndroidResponse<LoginInfo>>

    @POST("/user/login")
    @FormUrlEncoded
    fun testWAResponseLiveData(
        @Field("username") userName: String,
        @Field("password") password: String
    ): LiveData<Response<WAndroidResponse<LoginInfo>>>

}