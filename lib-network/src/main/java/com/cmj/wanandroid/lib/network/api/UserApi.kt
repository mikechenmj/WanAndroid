package com.cmj.wanandroid.lib.network.api

import com.cmj.wanandroid.lib.network.bean.PageModule
import com.cmj.wanandroid.lib.network.bean.LoginInfo
import com.cmj.wanandroid.lib.network.bean.Message
import com.cmj.wanandroid.lib.network.bean.User
import com.cmj.wanandroid.lib.network.bean.User.CoinInfo
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {

    @POST("/user/login")
    @FormUrlEncoded
    fun login(@Field("username") userName: String, @Field("password") password: String): Call<WAndroidResponse<LoginInfo>>

    @POST("/user/register")
    @FormUrlEncoded
    fun register(
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("repassword") rePassword: String
    ): Call<WAndroidResponse<LoginInfo>>

    @GET("/user/logout/json")
    fun logout(): Call<WAndroidResponse<Any?>>

    @GET("/coin/rank/{page}/json")
    fun coinRank(@Path("page") page: Int): Call<WAndroidResponse<PageModule<CoinInfo>>>

    @GET("/lg/coin/userinfo/json")
    fun userCoin(@Path("page") page: Int): Call<WAndroidResponse<PageModule<CoinInfo>>>

    @GET("/user/lg/userinfo/json")
    fun userInfo(): Call<WAndroidResponse<User>>

    @GET("/message/lg/count_unread/json")
    fun messageCountUnread(): Call<WAndroidResponse<Int>>

    @GET("/message/lg/unread_list/{page}/json")
    fun messageUnreadList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
    ): Call<WAndroidResponse<PageModule<Message>>>

    @GET("/message/lg/readed_list/{page}/json")
    fun messageReadList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
    ): Call<WAndroidResponse<PageModule<Message>>>
}