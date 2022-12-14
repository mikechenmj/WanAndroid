package com.cmj.wanandroid.user

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.network.bean.LoginInfo
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class UserViewModel (app: Application): BaseViewModel(app) {

    fun loginAsync(username: String, password: String): Deferred<Result<LoginInfo>> {
        return viewModelScope.async { UserRepository.login(username, password) }
    }

    fun registerAsync(username: String, password: String, rePassword: String): Deferred<Result<LoginInfo>> {
        return viewModelScope.async { UserRepository.register(username, password, rePassword) }
    }
}