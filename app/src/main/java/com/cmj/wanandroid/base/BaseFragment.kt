package com.cmj.wanandroid.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.kt.genericBinding
import com.cmj.wanandroid.ui.LoadingDialog
import com.cmj.wanandroid.kt.genericActivityViewModels
import com.cmj.wanandroid.kt.genericViewModels
import com.cmj.wanandroid.kt.inflate

abstract class BaseFragment<VM : ViewModel, AVM : ViewModel, VB : ViewBinding> : Fragment() {

    protected open val viewModel by genericViewModels<VM>(onInitialized = {
        return@genericViewModels false
    })

    protected open val activityViewModel by genericActivityViewModels<AVM>(onInitialized = {
        return@genericActivityViewModels false
    }, index = 1)

    private val dialog by lazy {
        LoadingDialog()
    }

    protected val viewLifecycle: Lifecycle
        get() {
            return viewLifecycleOwner.lifecycle
        }

    protected val viewLifecycleScope: LifecycleCoroutineScope
        get() {
            return viewLifecycle.coroutineScope
        }

    protected val binding by genericBinding<VB>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflate<VB>(this, inflater, container)
    }

    protected fun showLoading() {
        if (!dialog.isAdded) {
            dialog.show(childFragmentManager, this::class.java.name)
        }
    }

    protected fun hideLoading() {
        if (dialog.isAdded) {
            dialog.dismiss()
        }
    }

}
