package com.cmj.wanandroid.lib.base.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.lib.base.BaseActivity
import com.cmj.wanandroid.lib.base.databinding.ActivityWebBinding
import com.cmj.wanandroid.lib.base.ui.getStatusBarHeight

abstract class AbsWebActivity<VM: ViewModel> : BaseActivity<VM, ActivityWebBinding>() {

    companion object {
        private val OVERRIDE_URL_HOST_KEY = arrayOf("uland.taobao", "www.jd", "www.pinduoduo")
        const val EXTRA_URL = "extra_url"
    }

    private var decorShown = false

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = intent.getStringExtra(EXTRA_URL) ?: throw IllegalArgumentException("EXTRA_URL can not be null")
        binding.statusBarPlaceHolder.apply {
            val params = layoutParams as ViewGroup.MarginLayoutParams
            params.height = getStatusBarHeight().toInt()
            layoutParams = params
        }

        binding.web.apply {
            var touching = false
            setOnTouchListener { v, event ->
                touching = event.action == MotionEvent.ACTION_UP
                false
            }
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (touching) return@setOnScrollChangeListener
                val show = scrollY - oldScrollY > 0
                if (show) {
                    showDecor(false)
                } else {
                    showDecor(true)
                }
            }
            initWebSettings(settings)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    binding.webProgress.isVisible = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webProgress.isVisible = false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    if (request != null) {
                        if (OVERRIDE_URL_HOST_KEY.find { request.url.toString().contains(it) } != null) {
                            return true
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.webProgress.progress = newProgress
                }
            }
            loadUrl(url)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            showDecor(show = true, force = true)
        }
    }

    private fun showDecor(show: Boolean, force: Boolean = false) {
        if (!force && decorShown == show) return
        decorShown = show
        val systemUiVisibility = if (show) {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        window.decorView.systemUiVisibility = systemUiVisibility
        onDecorChange(show)
    }

   protected open fun onDecorChange(show: Boolean) {

   }

    private fun initWebSettings(settings: WebSettings) {
        settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true

            useWideViewPort = true
            loadWithOverviewMode = true

            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false

            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            defaultTextEncodingName = "utf-8"
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        }
    }

    override fun onBackPressed() {
        if (binding.web.canGoBack()) {
            binding.web.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.web.destroy()
    }
}