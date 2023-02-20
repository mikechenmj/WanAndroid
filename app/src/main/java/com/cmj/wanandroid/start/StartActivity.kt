package com.cmj.wanandroid.start

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.content.MainActivity
import com.cmj.wanandroid.databinding.LayoutStartBinding
import com.cmj.wanandroid.lib.base.BaseActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StartActivity : BaseActivity<ViewModel, LayoutStartBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                delay(1000)
            }
            startActivity(Intent(this@StartActivity, MainActivity::class.java))
            finish()
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}