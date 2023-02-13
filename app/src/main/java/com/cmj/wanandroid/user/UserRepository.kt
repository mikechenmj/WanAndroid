package com.cmj.wanandroid.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cmj.wanandroid.base.page.NormalPagingSource
import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.api.UserApi
import com.cmj.wanandroid.network.bean.*
import com.cmj.wanandroid.network.kt.resultWABodyCall
import kotlinx.coroutines.flow.Flow
import retrofit2.await

object UserRepository {

    private val userApi = NetworkEngine.createApi(UserApi::class.java)

    suspend fun login(username: String, password: String): Result<LoginInfo> {
        return userApi.login(username, password).resultWABodyCall().await()
    }

    suspend fun logout() = userApi.logout().resultWABodyCall().await()


    suspend fun register(
        username: String,
        password: String,
        rePassword: String
    ): Result<LoginInfo> {
        return userApi.register(username, password, rePassword).resultWABodyCall().await()
    }

    suspend fun userInfo(): Result<User> {
        return userApi.userInfo().resultWABodyCall().await()
    }

    fun isLoggedIn() = NetworkEngine.isLoggedIn()

    fun messageReadListFlow(pageSize: Int = 20): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    userApi.messageReadList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }

    fun messageUnReadListFlow(pageSize: Int = 20): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = pageSize),
            pagingSourceFactory = {
                NormalPagingSource { page, size ->
                    userApi.messageUnreadList(page, size).resultWABodyCall().await().getOrThrow()
                }
            }
        ).flow
    }
}