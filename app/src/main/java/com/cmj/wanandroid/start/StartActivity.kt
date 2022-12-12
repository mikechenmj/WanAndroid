package com.cmj.wanandroid.start

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.user.UserActivity
import com.cmj.wanandroid.content.ContentActivity

class StartActivity : BaseActivity<StartViewModel, ViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        start()
    }

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return null
    }

    private fun start() {
        if (viewModel.isLoggedIn()) {
            startActivity(Intent(this, ContentActivity::class.java))
        } else {
            startActivity(Intent(this, UserActivity::class.java))
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}