package com.cmj.wanandroid.lib.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.coroutineScope
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.common.log.LogMan
import com.cmj.wanandroid.lib.base.kt.genericBinding
import com.cmj.wanandroid.lib.base.kt.genericActivityViewModels
import com.cmj.wanandroid.lib.base.kt.genericViewModels
import com.cmj.wanandroid.lib.base.kt.inflate
import com.cmj.wanandroid.lib.base.ui.LoadingDialog

abstract class BaseFragment<VM : ViewModel, AVM : ViewModel, VB : ViewBinding> : Fragment() {

    init {
        LogMan.i("BaseFragment", "${this::class.java.simpleName} init")
    }

    protected val viewModel by genericViewModels<VM>(onInitialized = {
        return@genericViewModels false
    })

    protected val activityViewModel by genericActivityViewModels<AVM>(onInitialized = {
        return@genericActivityViewModels false
    }, index = 1)

    private var dialog: LoadingDialog? = null  //设置为可空是因为 dialog dismiss 后，leakCanary 会判断 dialog 泄漏。

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
        if (dialog == null) dialog = LoadingDialog()
        try {
            dialog!!.show(childFragmentManager, this::class.java.name)
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
}

data class ChildFragment(@StringRes val titleRes: Int, val clazz: Class<out Fragment>)
