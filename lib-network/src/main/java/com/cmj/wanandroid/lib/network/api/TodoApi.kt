package com.cmj.wanandroid.lib.network.api

import com.cmj.wanandroid.lib.network.bean.PageModule
import com.cmj.wanandroid.lib.network.bean.Todo
import com.cmj.wanandroid.lib.network.bean.WAndroidResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoApi {

    @POST("/lg/todo/add/json")
    @FormUrlEncoded
    fun addTodo(
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("type") type: Int,
        @Field("date") date: String? = null,
        @Field("priority") priority: String? = null,
    ): Call<WAndroidResponse<Todo>>

    @POST("/lg/todo/update/{id}/json")
    @FormUrlEncoded
    fun updateTodo(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("content") content: String,
        @Field("date") date: String,
        @Field("status") status: String, //0未完成,1完成
        @Field("type") type: Int,
        @Field("priority") priority: String? = null,
    ): Call<WAndroidResponse<Todo>>

    @POST("/lg/todo/delete/{id}/json")
    fun deleteTodo(
        @Path("id") id: Int
    ): Call<WAndroidResponse<Any?>>

    @POST("/lg/todo/done/{id}/json")
    fun doneTodo(
        @Path("id") id: Int,
        @Field("status") status: String, //0未完成,1完成
    ): Call<WAndroidResponse<Todo>>

    @GET("/lg/todo/v2/list/{page}/json")
    fun todoList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): Call<WAndroidResponse<PageModule<Todo>>>
}