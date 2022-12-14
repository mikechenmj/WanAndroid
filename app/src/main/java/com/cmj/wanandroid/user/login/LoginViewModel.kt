package com.cmj.wanandroid.user.login

import android.app.Application
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.kt.castAndTryEmit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(app: Application) : BaseViewModel(app) {

    val passwordCoverFlow: StateFlow<Boolean> = MutableStateFlow(true)

    fun setPasswordCover(cover: Boolean) {
        passwordCoverFlow.castAndTryEmit(cover)
    }
}