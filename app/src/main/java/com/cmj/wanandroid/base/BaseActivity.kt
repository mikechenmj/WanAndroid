package com.cmj.wanandroid.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.kt.genericBinding
import com.cmj.wanandroid.ui.LoadingDialog
import com.cmj.wanandroid.kt.genericViewModels

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected val viewModel by genericViewModels<VM> { provideViewModelFactory() ?: defaultViewModelProviderFactory }
    protected val binding by genericBinding<VB>()

    private var dialog: LoadingDialog? = null  //设置为可空是因为 dialog dismiss 后，leakCanary 会判断 dialog 泄漏。

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val view = onCreateView(LayoutInflater.from(this), savedInstanceState)
        if (view != null) {
            setContentView(view)
            onViewCreated(view, savedInstanceState)
        }
    }

    protected open fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    protected open fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    protected fun showLoading() {
        if (dialog == null) dialog = LoadingDialog()
        try {
            dialog!!.show(supportFragmentManager, this::class.java.name)
        } catch (e: Exception) {
            LogMan.w(this::class.java.simpleName, "hideLoading failed: $e")
        }
    }

    protected fun hideLoading() {
        if (dialog == null) return
        try {
            dialog!!.dismiss()
        } catch (e: Exception) {
            LogMan.w(this::class.java.simpleName, "hideLoading failed: $e")
        }
        dialog = null
    }

    open fun provideViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }
}
