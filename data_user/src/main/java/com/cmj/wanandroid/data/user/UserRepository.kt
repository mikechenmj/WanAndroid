package com.cmj.wanandroid.data.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cmj.wanandroid.data.user.api.UserApi
import com.cmj.wanandroid.lib.base.page.NormalPagingSource
import com.cmj.wanandroid.lib.network.NetworkEngine
import com.cmj.wanandroid.data.user.bean.LoginInfo
import com.cmj.wanandroid.data.user.bean.Message
import com.cmj.wanandroid.data.user.bean.User
import com.cmj.wanandroid.lib.network.kt.resultWABodyCall
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