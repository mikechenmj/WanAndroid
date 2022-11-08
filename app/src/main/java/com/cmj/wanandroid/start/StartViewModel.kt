package com.cmj.wanandroid.start

import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.user.UserRepository

class StartViewModel: ViewModel() {

    fun isLoggedIn(): Boolean {
        return UserRepository.isLoggedIn()
    }
}