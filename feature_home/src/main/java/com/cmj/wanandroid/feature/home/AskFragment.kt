package com.cmj.wanandroid.feature.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.ContentListAdapter
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class AskFragment : AbsContentPagingFragment<ViewModel, AskViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.askListFlow()
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(false, authorOrShareUser = false)
    }
}