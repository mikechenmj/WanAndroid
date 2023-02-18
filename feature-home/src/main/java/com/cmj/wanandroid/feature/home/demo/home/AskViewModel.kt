package com.cmj.wanandroid.feature.home.demo.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.data.content.ContentRepository
import com.cmj.wanandroid.data.content.ContentViewModel
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class AskViewModel(app: Application) : ContentViewModel(app) {

    private var askListFlow: Flow<PagingData<Content>>? = null

    fun askListFlow(): Flow<PagingData<Content>> {
        return askListFlow ?: ContentRepository.askListFlow(20).cachedIn(viewModelScope).also { askListFlow = it }
    }
}