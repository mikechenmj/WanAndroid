package com.cmj.wanandroid.content.search

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.ContentListAdapter
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class SearchResultFragment : AbsContentPagingFragment<ViewModel, SearchViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.queryArticleListFlow
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig()
    }
}