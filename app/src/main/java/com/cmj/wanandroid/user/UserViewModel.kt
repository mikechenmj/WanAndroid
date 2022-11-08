package com.cmj.wanandroid.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmj.wanandroid.network.bean.User
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class UserViewModel : ViewModel() {

    fun loginAsync(username: String, password: String): Deferred<Result<User>> {
        return viewModelScope.async { UserRepository.login(username, password) }
    }

    fun registerAsync(username: String, password: String, rePassword: String): Deferred<Result<User>> {
        return viewModelScope.async { UserRepository.register(username, password, rePassword) }
    }
}