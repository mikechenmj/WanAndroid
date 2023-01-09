package com.cmj.wanandroid.user

import com.cmj.wanandroid.network.NetworkEngine
import com.cmj.wanandroid.network.bean.LoginInfo
import com.cmj.wanandroid.network.api.UserApi
import com.cmj.wanandroid.network.kt.resultWABodyCall
import com.tencent.mmkv.MMKV
import retrofit2.await

object UserRepository {

    private val userApi = NetworkEngine.createApi(UserApi::class.java)

    suspend fun login(username: String, password: String): Result<LoginInfo> {
        return userApi.login(username, password).resultWABodyCall().await()
    }

    suspend fun logout(): Result<Any?> {
        return userApi.logout().resultWABodyCall().await()
    }

    suspend fun register(username: String, password: String, rePassword: String): Result<LoginInfo> {
        return userApi.register(username, password, rePassword).resultWABodyCall().await()
    }
}