package com.cmj.wanandroid.feature.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class RecommendFragment : AbsContentPagingFragment<ViewModel, RecommendViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.articleListFlow()
    }
}