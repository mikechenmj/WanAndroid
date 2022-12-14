package com.cmj.wanandroid.start

import android.app.Application
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.user.UserRepository

class StartViewModel(app: Application) : BaseViewModel(app) {

    fun isLoggedIn(): Boolean {
        return UserRepository.isLoggedIn()
    }
}