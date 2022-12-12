package com.cmj.wanandroid.network.api

import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.network.bean.PageModule
import com.cmj.wanandroid.network.bean.Banner
import com.cmj.wanandroid.network.bean.CollectedArticle
import com.cmj.wanandroid.network.bean.FriendWeb
import com.cmj.wanandroid.network.bean.Hotkey
import com.cmj.wanandroid.network.bean.Navi
import com.cmj.wanandroid.network.bean.QAComments
import com.cmj.wanandroid.network.bean.ShareArticleInfo
import com.cmj.wanandroid.network.bean.Tree
import com.cmj.wanandroid.network.bean.WAndroidResponse
import com.cmj.wanandroid.network.bean.Web
import com.cmj.wanandroid.network.bean.WxChapter
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query


interface ContentApi {

    @GET("/article/list/{page}/json")
    fun articleList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Query("order_type") orderType: Int = 0,//0倒序，1正序
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/article/list/{page}/json")
    fun articleListWithId(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Query("cid") cid: Int? = null,
        @Query("order_type") orderType: Int = 0,//0倒序，1正序
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/article/list/{page}/json")
    fun articleListWithAuthor(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Query("author") author: Int? = null,
        @Query("order_type") orderType: Int = 0,//0倒序，1正序
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/article/top/json")
    fun articleTop(): Call<WAndroidResponse<List<Content>>>

    @POST("/article/query/{page}/json")
    @FormUrlEncoded
    fun queryArticle(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Field("k") key: String
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/navi/json")
    fun navi(): Call<WAndroidResponse<List<Navi>>>

    @GET("/banner/json")
    fun banner(): Call<WAndroidResponse<List<Banner>>>

    @GET("/friend/json")
    fun friendWeb(): Call<WAndroidResponse<List<FriendWeb>>>

    @GET("/hotkey/json")
    fun hotkey(): Call<WAndroidResponse<List<Hotkey>>>

    @GET("/tree/json")
    fun tree(): Call<WAndroidResponse<List<Tree>>>

    @GET("/project/tree/json")
    fun projectTree(): Call<WAndroidResponse<List<Tree>>>

    @GET("/project/list/{page}/json")
    fun projectList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Query("cid") cid: Int? = null
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/lg/collect/list/{page}/json")
    fun collectList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
    ): Call<WAndroidResponse<PageModule<CollectedArticle>>>

    @POST("/lg/collect/{id}/json")
    fun collect(@Path("id") id: Int): Call<WAndroidResponse<Any?>>

    @POST("/lg/collect/add/json")
    @FormUrlEncoded
    fun collectOutside(
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("link") link: String
    ): Call<WAndroidResponse<CollectedArticle>>

    @POST("/lg/collect/user_article/update/{id}/json")
    @FormUrlEncoded
    fun updateCollect(
        @Path("id") id: Int,
        @Field("title") title: String,
        @Field("author") author: String,
        @Field("link") link: String
    ): Call<WAndroidResponse<CollectedArticle>>

    @POST("/lg/uncollect_originId/{id}/json")
    fun unCollectOriginId(@Path("id") id: Int): Call<WAndroidResponse<Any?>>

    @POST("/lg/uncollect/{id}/json")
    @FormUrlEncoded
    fun unCollect(
        @Path("id") id: Int,
        @Field("originId") originId: Int = -1
    ): Call<WAndroidResponse<Any?>>

    @GET("lg/collect/usertools/json")
    fun collectWebList(): Call<WAndroidResponse<List<Web>>>

    @POST("/lg/collect/addtool/json")
    @FormUrlEncoded
    fun collectWeb(
        @Field("name") name: String,
        @Field("link") link: String
    ): Call<WAndroidResponse<Web>>

    @POST("/lg/collect/updatetool/json")
    @FormUrlEncoded
    fun updateCollectWeb(
        @Field("id") id: Int,
        @Field("name") name: String,
        @Field("link") link: String
    ): Call<WAndroidResponse<Web>>

    @POST("/lg/collect/deletetool/json")
    @FormUrlEncoded
    fun unCollectWeb(
        @Field("id") id: Int
    ): Call<WAndroidResponse<Web>>

    @GET("/user_article/list/{page}/json")
    fun userArticleList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/user/{id}/share_articles/{page}/json")
    fun shareArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): Call<WAndroidResponse<ShareArticleInfo>>

    @GET("/user/lg/private_articles/{page}/json")
    fun privateArticleList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): Call<WAndroidResponse<ShareArticleInfo>>

    @POST("/lg/user_article/delete/{id}/json")
    fun deletePrivateArticle(@Path("id") id: Int): Call<WAndroidResponse<Any?>>

    @POST("/lg/user_article/add/json")
    @FormUrlEncoded
    fun addUserArticle(
        @Field("title") title: String,
        @Field("link") link: String,
    ): Call<WAndroidResponse<Any?>>

    @GET("/wenda/list/{page}/json/")
    fun qAList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/wenda/comments/{id}/json")
    fun qACommentsList(@Path("id") id: Int): Call<WAndroidResponse<PageModule<QAComments>>>

    @GET("/wxarticle/chapters/json")
    fun wxArticleChapters(): Call<WAndroidResponse<List<WxChapter>>>

    @GET("/wxarticle/list/{id}/{page}/json")
    fun wxArticleList(
        @Path("id") id: Int,
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
        @Query("k") k: String? = null
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/article/listproject/{page}/json")
    fun projectList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int? = null,
    ): Call<WAndroidResponse<PageModule<Content>>>

    @GET("/chapter/547/sublist/json")
    fun chapterList(): Call<WAndroidResponse<List<Tree>>>

}

