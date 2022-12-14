package com.cmj.wanandroid.content.home

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPageFragment
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class AskFragment : AbsContentPageFragment<ViewModel, AskViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.askListFlow()
    }
}