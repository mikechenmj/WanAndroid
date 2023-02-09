package com.cmj.wanandroid.content.private

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.FragmentStubActivity
import com.cmj.wanandroid.content.AbsContentPagingFragment
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.kt.getOrHandleError
import com.cmj.wanandroid.network.bean.Content
import kotlinx.android.synthetic.main.fragment_ask.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PrivateArticleFragment : AbsContentPagingFragment<ViewModel, PrivateArticleViewModel>() {

    companion object {
        fun start(context: Context) {
            FragmentStubActivity.start(
                context,
                PrivateArticleFragment::class.java,
                context.getString(R.string.user_share_label),
                null
            )
        }
    }

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.privateArticleListFlow()
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(
            tags = true,
            authorOrShareUser = false,
            date = true,
            star = false,
            delete = true
        )
    }

    override fun onDeleteClick(content: Content, position: Int) {
        super.onDeleteClick(content, position)
        viewLifecycleScope.launch {
            showLoading()
            activityViewModel.deletePrivateArticle(content).getOrHandleError(requireContext())
            contentAdapter.refresh()
            hideLoading()
        }
    }

}