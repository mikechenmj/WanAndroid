package com.cmj.wanandroid.base.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Html
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.base.BaseActivity
import com.cmj.wanandroid.content.ContentViewModel
import com.cmj.wanandroid.content.search.SearchActivity
import com.cmj.wanandroid.databinding.ActivityWebViewBinding
import com.cmj.wanandroid.kt.handleIfError
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.ui.getStatusBarHeight
import kotlinx.coroutines.launch

class ContentWebActivity : BaseActivity<ContentViewModel, ActivityWebViewBinding>() {

    companion object {
        private val OVERRIDE_URL_HOST_KEY = arrayOf("uland.taobao", "www.jd", "www.pinduoduo")
        private const val EXTRA_CONTENT = "extra_content"

        fun start(context: Context, content: Content) {
            context.startActivity(Intent(context, ContentWebActivity::class.java).apply {
                putExtra(EXTRA_CONTENT, content)
            })
        }

        fun start(context: Context, intent: Intent, content: Content) {
            context.startActivity(intent.apply {
                setClassName(context.packageName, ContentWebActivity::class.java.name)
                putExtra(EXTRA_CONTENT, content)
            })
        }
    }

    private var decorShown = false

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val content = intent.getParcelableExtra<Content>(EXTRA_CONTENT) ?: throw IllegalArgumentException("EXTRA_CONTENT can not be null")
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
            loadUrl(content.link)
        }
        binding.authorOrShareUser.text = Html.fromHtml(content.author.ifBlank { content.shareUser }).toString()
        binding.star.apply {
            isSelected = content.collect
            setOnClickListener {
                if (content.collect == isSelected) {
                    isSelected = !isSelected
                    handleStar(content)
                }
            }
        }
    }

    private fun handleStar(content: Content) {
        lifecycleScope.launch {
            val result = if (content.collect) viewModel.unStar(content) else viewModel.star(content)
            if (!result.handleIfError(this@ContentWebActivity)) content.collect = !content.collect
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
        val translateY = if (show) 0 else binding.bottomMenu.height
        binding.bottomMenu.animate().translationY(translateY.toFloat()).start()
        binding.authorOrShareUser.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java).apply {
                putExtra(SearchActivity.EXTRA_SEARCH_HOTKEY, binding.authorOrShareUser.text)
                putExtra(SearchActivity.EXTRA_SEARCH_PERFORM, true)
            })
        }
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