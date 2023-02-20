package com.cmj.android.feature.mine

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.cmj.wanandroid.data.user.UserRepository
import com.cmj.wanandroid.lib.base.BaseViewModel
import com.cmj.wanandroid.lib.base.bean.LoginInfo
import com.cmj.wanandroid.lib.base.bean.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

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

    fun userInfoAsync(): Deferred<Result<User>> {
        return viewModelScope.async { UserRepository.userInfo() }
    }
}