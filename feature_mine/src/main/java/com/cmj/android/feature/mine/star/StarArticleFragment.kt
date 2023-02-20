package com.cmj.android.feature.mine.star

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.android.feature.mine.R
import com.cmj.wanandroid.lib.base.FragmentStubActivity
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.ContentListAdapter
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.lib.base.kt.getOrHandleError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class StarArticleFragment : AbsContentPagingFragment<ViewModel, StarArticleViewModel>() {

    companion object {
        fun start(context: Context) {
            FragmentStubActivity.start(
                context,
                StarArticleFragment::class.java,
                context.getString(R.string.user_star_label),
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
            pagingAdapter.refresh()
            hideLoading()
        }
    }

}