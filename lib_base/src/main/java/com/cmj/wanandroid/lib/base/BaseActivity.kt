package com.cmj.wanandroid.lib.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.cmj.wanandroid.common.log.LogMan
import com.cmj.wanandroid.lib.base.kt.genericBinding
import com.cmj.wanandroid.lib.base.kt.genericViewModels
import com.cmj.wanandroid.lib.base.ui.LoadingDialog
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        @JvmStatic
        val fallbackOnBackPressedField by lazy {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                null
            } else {
                // OnBackPressedDispatcher 中只有fallbackOnBackPressed是Runnable
                // 这样可以避免代码混淆带来的影响
                OnBackPressedDispatcher::class.java.declaredFields.find {
                    it.type.isAssignableFrom(Runnable::class.java)
                }
            }
        }
    }

    init {
        LogMan.i("BaseActivity", "${this::class.java.simpleName} init")
    }

    private var fallbackOnBackPressed: Runnable? = null

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
        fixAndroidQMemoryLeak()
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

    private fun fixAndroidQMemoryLeak() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) return
        fallbackOnBackPressedField?.runCatching {
            isAccessible = true
            // 缓存默认的 fallbackOnBackPressed，如果不是TaskRoot还可以直接用它
            fallbackOnBackPressed = get(onBackPressedDispatcher) as? Runnable
            if (fallbackOnBackPressed != null) {
                // 替换默认的 fallbackOnBackPressed
                set(onBackPressedDispatcher, Runnable {
                    onFallbackOnBackPressed()
                })
            }
            fallbackOnBackPressedField?.isAccessible = false
        }?.onFailure {
            fallbackOnBackPressedField?.isAccessible = false
        }
    }

    /**
     * FragmentActivity等都是利用[mOnBackPressedDispatcher]addCallback完成Fragment pop，
     * 并且会优先于[OnBackPressedDispatcher]#mFallbackOnBackPressed执行
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun onFallbackOnBackPressed() {
        // 如果不是TaskRoot，不存在内存泄漏，执行原有函数就可以
        if (!isTaskRoot) {
            fallbackOnBackPressed?.run()
            return
        }
        // actionBar#collapseActionView 属于私有函数，所以就不要在Activity中使用[android.app.ActionBar]
        // if (actionBar != null && actionBar.collapseActionView()) return
        if (!fragmentManager.isStateSaved && fragmentManager.popBackStackImmediate()) return
        finishAfterTransition()
    }
}
