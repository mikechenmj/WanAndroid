package com.cmj.wanandroid.user

import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.databinding.ActivityLoginBinding
import com.cmj.wanandroid.kt.findNavigationById

class UserActivity : BaseActivity<UserViewModel, ActivityLoginBinding>() {

    companion object {
        private val TAG = UserActivity::class.java.simpleName
    }

    fun navigateToRegister() {
        val controller = findNavigationById(R.id.navigationContent)?.navController.also {
            if (it == null) {
                LogMan.w(TAG, "NavigationController is null")
            }
        } ?: return
        if (controller.currentDestination?.id != R.id.loginFragment) return
        controller.navigate(R.id.action_loginFragment_to_registerFragment)
    }
}