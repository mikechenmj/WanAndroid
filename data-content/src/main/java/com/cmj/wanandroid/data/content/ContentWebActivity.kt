package com.cmj.wanandroid.data.content

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.data.content.databinding.ActivityContentMenuBinding
import com.cmj.wanandroid.lib.base.kt.handleIfError
import com.cmj.wanandroid.lib.base.web.AbsWebActivity
import kotlinx.coroutines.launch

class ContentWebActivity : AbsWebActivity<ContentViewModel>() {

    companion object {
        const val EXTRA_CONTENT = "extra_content"

        fun start(context: Context, content: Content) {
            context.startActivity(Intent(context, ContentWebActivity::class.java).apply {
                putExtra(EXTRA_CONTENT, content)
                putExtra(EXTRA_URL, content.link)
            })
        }

        fun start(context: Context, intent: Intent, content: Content) {
            context.startActivity(intent.apply {
                setClassName(context.packageName, ContentWebActivity::class.java.name)
                putExtra(EXTRA_CONTENT, content)
                putExtra(EXTRA_URL, content.link)
            })
        }
    }

    private lateinit var menuBinding: ActivityContentMenuBinding

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val content = intent.getParcelableExtra<Content>(EXTRA_CONTENT)
            ?: throw IllegalArgumentException("EXTRA_CONTENT can not be null")
        menuBinding =
            ActivityContentMenuBinding.inflate(LayoutInflater.from(this), binding.root, true)
        menuBinding.authorOrShareUser.text = content.validAuthor()
        menuBinding.star.apply {
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

    override fun onDecorChange(show: Boolean) {
        super.onDecorChange(show)
        val translateY = if (show) 0 else menuBinding.bottomMenu.height
        menuBinding.bottomMenu.animate().translationY(translateY.toFloat()).start()
        menuBinding.authorOrShareUser.setOnClickListener {
            // to do
//            startActivity(Intent(this, SearchActivity::class.java).apply {
//                putExtra(SearchActivity.EXTRA_SEARCH_HOTKEY, menuBinding.authorOrShareUser.text)
//                putExtra(SearchActivity.EXTRA_SEARCH_PERFORM, true)
//            })
        }
    }
}