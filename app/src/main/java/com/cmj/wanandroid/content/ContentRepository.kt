package com.cmj.wanandroid.content

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.api.ContentApi
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.network.bean.PageModule
import com.cmj.wanandroid.network.kt.resultWABodyCall
import kotlinx.coroutines.flow.Flow
import retrofit2.await

object ContentRepository {

    private val api = NetworkEngine.createApi(ContentApi::class.java)

    suspend fun star(content: Content) = api.collect(content.id).resultWABodyCall().await()
    suspend fun unStar(content: Content) = api.unCollectOriginId(content.id).resultWABodyCall().await()

    suspend fun banner() = api.banner().resultWABodyCall().await()

    fun articleListFlow(pageSize: Int = 20, orderType: Int = 0): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                ContentPagingSource(-1) { page, size ->
                    if (page == -1) {
                        val top = api.articleTop().resultWABodyCall().await().getOrThrow()
                        top.forEach { it.top = true }
                        PageModule(-1, 0, false, -1, top.size, top.size, top)
                    } else {
                        api.articleList(page, size, orderType).resultWABodyCall().await().getOrThrow()
                    }
                }
            }
        ).flow
    }

    fun articleListWithIdFlow(cid: Int, pageSize: Int = 20, orderType: Int = 0): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                ContentPagingSource { page, size ->
                    api.articleListWithId(page, size, cid, orderType).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    fun askListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                ContentPagingSource { page, size ->
                    api.qAList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    fun shareListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                ContentPagingSource { page, size ->
                    api.userArticleList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    suspend fun tree() = api.tree().resultWABodyCall().await()
    suspend fun projectTree() = api.projectTree().resultWABodyCall().await()

    suspend fun wxOfficial() = api.wxArticleChapters().resultWABodyCall().await()
    fun wxArticleListFlow(id: Int, pageSize: Int = 20, k: String? = null): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                ContentPagingSource { page, size ->
                    api.wxArticleList(id, page, size, k).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

}