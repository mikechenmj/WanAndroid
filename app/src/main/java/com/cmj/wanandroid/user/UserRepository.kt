package com.cmj.wanandroid.user

import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.bean.User
import com.cmj.wanandroid.network.api.UserApi
import com.cmj.wanandroid.network.kt.resultWABodyCall
import com.tencent.mmkv.MMKV
import retrofit2.await

object UserRepository {

    private const val USER_KEY = "user_key"

    private val userApi = NetworkEngine.createApi(UserApi::class.java)

    fun isLoggedIn(): Boolean {
        return NetworkEngine.isLoggerIn
    }

    suspend fun login(username: String, password: String): Result<User> {
        return userApi.login(username, password).resultWABodyCall().await().also {
            if (it.isSuccess) {
                updateUser(it.getOrThrow())
            }
        }
    }

    suspend fun logout(): Result<Any?> {
        return userApi.logout().resultWABodyCall().await().also {
            if (it.isSuccess) {
                updateUser(null)
            }
        }
    }

    suspend fun register(username: String, password: String, rePassword: String): Result<User> {
        return userApi.register(username, password, rePassword).resultWABodyCall().await().also {
            if (it.isSuccess) {
                updateUser(it.getOrThrow())
            }
        }
    }

    private fun updateUser(user: User?) {
        MMKV.defaultMMKV().encode(USER_KEY, user)
    }

    fun getUser(): User? {
        return MMKV.defaultMMKV().decodeParcelable(USER_KEY, User::class.java)
    }
}