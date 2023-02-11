package com.cmj.wanandroid.content.star

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.R
import com.cmj.wanandroid.base.FragmentStubActivity
import com.cmj.wanandroid.content.AbsContentPagingFragment
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.kt.getOrHandleError
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StarArticleFragment : AbsContentPagingFragment<ViewModel, StarArticleViewModel>() {

    companion object {
        fun start(context: Context) {
            FragmentStubActivity.start(
                context,
                StarArticleFragment::class.java,
                context.getString(R.string.user_star_label),
                null
            )
        }
    }

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.starArticleListFlow()
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
            activityViewModel.unStar(content).getOrHandleError(requireContext())
            contentAdapter.refresh()
            hideLoading()
        }
    }

}