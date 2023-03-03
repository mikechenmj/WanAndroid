package com.cmj.wanandroid.data.user

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.cmj.wanandroid.common.kt.doWhileSubscribed
import com.cmj.wanandroid.common.kt.emitFlow
import com.cmj.wanandroid.data.user.UserRepository
import com.cmj.wanandroid.lib.base.BaseViewModel
import com.cmj.wanandroid.data.user.bean.LoginInfo
import com.cmj.wanandroid.data.user.bean.User
import com.cmj.wanandroid.lib.network.NetworkUtil
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

class UserViewModel(app: Application) : BaseViewModel(app) {

    fun isLoggedIn() = UserRepository.isLoggedIn()

    fun loginAsync(username: String, password: String): Deferred<Result<LoginInfo>> {
        return viewModelScope.async { UserRepository.login(username, password) }
    }

    suspend fun logout() = UserRepository.logout()

    fun registerAsync(
        username: String,
        password: String,
        rePassword: String
    ): Deferred<Result<LoginInfo>> {
        return viewModelScope.async { UserRepository.register(username, password, rePassword) }
    }

    val userInfoFlow: SharedFlow<Result<User>> = MutableSharedFlow<Result<User>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                emitFlow(UserRepository.userInfoFlow())
            }
        }

    fun userInfoAsync(): Deferred<Result<User>> {
        return viewModelScope.async { UserRepository.userInfo() }
    }
}