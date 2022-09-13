package com.cmj.wanandroid.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.view.LoadingDialog
import com.gree.controller.kt.genericViewModels
import com.gree.themestore.viewbinding.genericBinding

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected val viewModel by genericViewModels<VM>(provideViewModelFactory())
    protected val binding by genericBinding<VB>()

    private val dialog by lazy {
        LoadingDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        val view = onCreateView(LayoutInflater.from(this), savedInstanceState)
        if (view != null) {
            setContentView(view)
            onViewCreated(view, savedInstanceState)
        }
    }

    open fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    protected open fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    protected open fun initData(savedInstanceState: Bundle?) {
    }

    protected fun showDialog() {
        dialog.show(supportFragmentManager, "")
    }

    protected fun hideDialog() {
        dialog.dismiss()
    }

    open fun provideViewModelFactory(): (() -> ViewModelProvider.Factory)? {
        return null
    }
}
