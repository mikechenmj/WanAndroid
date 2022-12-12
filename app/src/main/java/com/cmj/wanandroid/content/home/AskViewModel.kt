package com.cmj.wanandroid.content.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class AskViewModel : ViewModel() {

    private var askListFlow: Flow<PagingData<Content>>? = null

    fun askListFlow(): Flow<PagingData<Content>> {
        return askListFlow ?: ContentRepository.askListFlow(20).cachedIn(viewModelScope).also { askListFlow = it }
    }
}