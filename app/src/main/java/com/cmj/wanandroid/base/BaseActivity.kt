package com.cmj.wanandroid.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.log.LogMan
import com.cmj.wanandroid.kt.genericBinding
import com.cmj.wanandroid.ui.LoadingDialog
import com.cmj.wanandroid.kt.genericViewModels
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    init {
        LogMan.i("BaseActivity", "${this::class.java.simpleName} init")
    }

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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this)
                .setTitle(R.string.need_permission_title)
                .setRationale(R.string.need_permission_rationale)
                .build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int, options: Bundle?) {
        super.startActivityForResult(intent, requestCode, options)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
