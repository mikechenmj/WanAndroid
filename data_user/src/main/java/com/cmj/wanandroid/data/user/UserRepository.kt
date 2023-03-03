package com.cmj.wanandroid.data.user

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.cmj.wanandroid.common.CommonInitializer
import com.cmj.wanandroid.common.kt.doWhileSubscribed
import com.cmj.wanandroid.common.kt.emitFlow
import com.cmj.wanandroid.data.user.api.UserApi
import com.cmj.wanandroid.lib.base.page.NormalPagingSource
import com.cmj.wanandroid.lib.network.NetworkEngine
import com.cmj.wanandroid.data.user.bean.LoginInfo
import com.cmj.wanandroid.data.user.bean.Message
import com.cmj.wanandroid.data.user.bean.User
import com.cmj.wanandroid.data.user.db.UserDatabase
import com.cmj.wanandroid.lib.base.Constant.INT_INVALID
import com.cmj.wanandroid.lib.network.NetworkUtil
import com.cmj.wanandroid.lib.network.kt.resultWABodyCall
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.*
import retrofit2.await

object UserRepository {

    private const val KEY_USER_ID = "key_user_id"

    private val kv by lazy {
        MMKV.defaultMMKV()
    }

    private val database: UserDatabase =
        UserDatabase.getInstance(CommonInitializer.getContext())

    private val userDao = database.userDao()

    private val userApi = NetworkEngine.createApi(UserApi::class.java)

    suspend fun login(username: String, password: String): Result<LoginInfo> {
        return userApi.login(username, password).resultWABodyCall().await().also {
            val id = it.getOrNull()?.id ?: return@also
            kv.encode(KEY_USER_ID, id)
        }
    }

    suspend fun logout() = userApi.logout().resultWABodyCall().await().also {
        kv.encode(KEY_USER_ID, INT_INVALID)
    }


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

    suspend fun userInfoFlow(): Flow<Result<User>> {
        return flow {
            val id = kv.decodeInt(KEY_USER_ID)
            var cache: User? = null
            if (id != INT_INVALID) {
                cache = userDao.query(id)
                if (cache != null) {
                    emit(Result.success(cache))
                }
            }
            if (NetworkUtil.isConnected) {
                emit(userInfo().also {
                    val new = it.getOrNull() ?: return@also
                    if (cache != null && cache.id == new.id) {
                        userDao.updateSuspend(cache)
                    } else {
                        userDao.insertSuspend(new)
                    }
                })
            }
        }
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