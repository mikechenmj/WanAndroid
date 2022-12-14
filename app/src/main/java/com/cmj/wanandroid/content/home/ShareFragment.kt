package com.cmj.wanandroid.content.home


import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPageFragment
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class ShareFragment : AbsContentPageFragment<ViewModel, ShareViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.shareListFlow()
    }
}