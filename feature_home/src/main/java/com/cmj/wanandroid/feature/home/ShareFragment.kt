package com.cmj.wanandroid.feature.home


import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.data.content.AbsContentPagingFragment
import com.cmj.wanandroid.data.content.ContentListAdapter
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class ShareFragment : AbsContentPagingFragment<ViewModel, ShareViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.shareListFlow()
    }

    override fun contentConfig(): ContentListAdapter.ContentConfig {
        return ContentListAdapter.ContentConfig(false)
    }
}