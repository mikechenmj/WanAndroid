package com.cmj.wanandroid.feature.mine.private

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.feature.mine.R
import com.cmj.wanandroid.lib.base.FragmentStubActivity
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.ContentListAdapter
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class PrivateArticleFragment : AbsContentPagingFragment<ViewModel, PrivateArticleViewModel>() {

    companion object {
        fun start(context: Context) {
            FragmentStubActivity.start(
                context,
                PrivateArticleFragment::class.java,
                context.getString(R.string.user_share_label),
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
            pagingAdapter.refresh()
            hideLoading()
        }
    }

}