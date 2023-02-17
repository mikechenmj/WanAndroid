package com.cmj.wanandroid.content.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPagingFragment
import com.cmj.wanandroid.lib.network.bean.Content
import kotlinx.coroutines.flow.Flow

class RecommendFragment : AbsContentPagingFragment<ViewModel, RecommendViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.articleListFlow()
    }
}