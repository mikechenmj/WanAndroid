package com.cmj.wanandroid.content.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPagingFragment
import com.cmj.wanandroid.content.home.ContentListAdapter
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SearchResultFragment : AbsContentPagingFragment<ViewModel, SearchViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.queryArticleListFlow
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig()
    }
}