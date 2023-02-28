package com.cmj.wanandroid.feature.login.login

import android.app.Application
import com.cmj.wanandroid.lib.base.BaseViewModel
import com.cmj.wanandroid.common.kt.castAndTryEmit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(app: Application) : BaseViewModel(app) {

    val passwordCoverFlow: StateFlow<Boolean> = MutableStateFlow(true)

    fun setPasswordCover(cover: Boolean) {
        passwordCoverFlow.castAndTryEmit(cover)
    }
}