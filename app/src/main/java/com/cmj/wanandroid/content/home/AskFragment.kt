package com.cmj.wanandroid.content.home

import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPageFragment
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class AskFragment : AbsContentPageFragment<AskViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return viewModel.askListFlow()
    }
}