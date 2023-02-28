package com.cmj.wanandroid.data.content

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cmj.wanandroid.common.CommonInitializer
import com.cmj.wanandroid.data.content.api.ContentApi
import com.cmj.wanandroid.data.content.bean.Banner
import com.cmj.wanandroid.lib.base.page.NormalPagingSource
import com.cmj.wanandroid.lib.network.NetworkEngine
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.data.content.db.ContentDatabase
import com.cmj.wanandroid.lib.network.NetworkUtil
import com.cmj.wanandroid.lib.network.bean.PageModule
import com.cmj.wanandroid.lib.network.kt.resultWABodyCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.await

object ContentRepository {

    private val database: ContentDatabase =
        ContentDatabase.getInstance(CommonInitializer.getContext())

    private val bannerDao = database.bannerDao()

    private val api = NetworkEngine.createApi(ContentApi::class.java)

    suspend fun star(content: Content) = api.collect(content.id).resultWABodyCall().await()
    suspend fun unStar(content: Content) =
        api.unCollectOriginId(content.id).resultWABodyCall().await()

    suspend fun banner() = api.banner().resultWABodyCall().await()

    fun bannerFlow(): Flow<Result<List<Banner>>> {
        return flow {
            val cache = bannerDao.banners()
            if (cache.isNotEmpty()) {
                emit(Result.success(cache.toList()))
            }
            if (NetworkUtil.isConnected) {
                emit(banner().also {
                    val new = it.getOrNull()?.toTypedArray() ?: return@also
                    bannerDao.deleteSuspend(*cache)
                    bannerDao.insertSuspend(*new)
                })
            }
        }
    }

    fun articleListFlow(pageSize: Int = 20, orderType: Int = 0): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource(-1) { page, size ->
                    if (page == -1) {
                        val top = api.articleTop().resultWABodyCall().await().getOrThrow()
                        top.forEach { it.top = true }
                        PageModule(
                            -1,
                            0,
                            false,
                            -1,
                            top.size,
                            top.size,
                            top
                        )
                    } else {
                        api.articleList(page, size, orderType).resultWABodyCall().await()
                            .getOrThrow()
                    }
                }
            }
        ).flow
    }

    fun articleListWithIdFlow(
        cid: Int,
        pageSize: Int = 20,
        orderType: Int = 0
    ): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    api.articleListWithId(page, size, cid, orderType).resultWABodyCall().await()
                        .getOrThrow()
                }
            }
        ).flow
    }

    fun askListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    api.qAList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    fun shareListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    api.userArticleList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    suspend fun tree() = api.tree().resultWABodyCall().await()

    suspend fun projectTree() = api.projectTree().resultWABodyCall().await()
    fun projectListFlow(id: Int, pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource(1) { page, size ->
                    api.projectList(page, size, id).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    suspend fun wxOfficial() = api.wxArticleChapters().resultWABodyCall().await()
    fun wxArticleListFlow(
        id: Int,
        pageSize: Int = 20,
        k: String? = null
    ): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    api.wxArticleList(id, page, size, k).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    suspend fun hotKey() = api.hotkey().resultWABodyCall().await()
    fun queryArticleListFlow(pageSize: Int = 20, k: String): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    api.queryArticle(page, size, k).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    fun privateArticleListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource(1) { page, size ->
                    api.privateArticleList(page, size).resultWABodyCall().await()
                        .getOrThrow().shareArticles
                }
            }
        ).flow
    }

    suspend fun deletePrivateArticle(content: Content) =
        api.deletePrivateArticle(content.id).resultWABodyCall().await()

    fun starArticleListFlow(pageSize: Int = 20): Flow<PagingData<Content>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    val module = api.collectList(page, size).resultWABodyCall().await().getOrThrow()
                    PageModule(
                        module.curPage, module.offset, module.over, module.pageCount,
                        module.size, module.total, module.datas.map { it.toContent() }
                    )
                }
            }
        ).flow
    }
}