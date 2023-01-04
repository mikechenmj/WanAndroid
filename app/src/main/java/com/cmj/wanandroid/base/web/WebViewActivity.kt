package com.cmj.wanandroid.base.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModel
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.databinding.ActivityWebViewBinding
import com.cmj.wanandroid.ui.getStatusBarHeight

class WebViewActivity : BaseActivity<ViewModel, ActivityWebViewBinding>() {

    companion object {
        private val OVERRIDE_URL_HOST_KEY = arrayOf("uland.taobao", "www.jd", "www.pinduoduo")
        private const val EXTRA_URL = "extra_url"

        fun start(context: Context, url: String) {
            context.startActivity(Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            })
        }

        fun start(context: Context, intent: Intent, url: String) {
            context.startActivity(intent.apply {
                setClassName(context.packageName, WebViewActivity::class.java.name)
                putExtra(EXTRA_URL, url)
            })
        }
    }

    private var statusBarShown = false

    private val marginTop by lazy { getStatusBarHeight().toInt() }

    private var lastScrollY = 0
    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL) ?: throw IllegalArgumentException("EXTRA_URL can not be null")
        binding.web.apply {
            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val show = scrollY - lastScrollY >= 0
                lastScrollY = scrollY
                if (show) {
                    showStatusBar(false)
                } else {
                    showStatusBar(true)
                }
            }
            initWebSettings(settings)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
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
            loadUrl(url)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            showStatusBar(show = true, force = true)
        }
    }

    private fun showStatusBar(show: Boolean, force: Boolean = false) {
        if (!force && statusBarShown == show) return
        statusBarShown = show
        val params = binding.web.layoutParams as ViewGroup.MarginLayoutParams
        if (show) {
            params.topMargin = marginTop
            lastScrollY += marginTop
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            params.topMargin = 0
            lastScrollY -= marginTop
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
        binding.web.layoutParams = params
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
}