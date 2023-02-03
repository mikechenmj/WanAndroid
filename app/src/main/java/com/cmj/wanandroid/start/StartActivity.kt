package com.cmj.wanandroid.start

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.content.ContentActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartActivity : BaseActivity<ViewModel, ViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delay(1000)
            }
            startActivity(Intent(this@StartActivity, ContentActivity::class.java))
            finish()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return null
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}