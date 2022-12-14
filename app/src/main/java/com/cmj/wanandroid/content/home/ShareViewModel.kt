package com.cmj.wanandroid.content.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.content.ContentViewModel
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class ShareViewModel(application: Application) : ContentViewModel(application) {

    private var shareListFlow: Flow<PagingData<Content>>? = null

    fun shareListFlow(): Flow<PagingData<Content>> {
        return shareListFlow ?: ContentRepository.shareListFlow(20).cachedIn(viewModelScope).also { shareListFlow = it }
    }

}