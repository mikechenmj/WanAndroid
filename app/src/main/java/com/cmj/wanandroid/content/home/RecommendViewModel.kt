package com.cmj.wanandroid.content.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.kt.castAndEmit
import com.cmj.wanandroid.kt.doWhileSubscribed
import com.cmj.wanandroid.network.NetworkUtil
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

class RecommendViewModel : ViewModel() {

    private var articleListFlow: Flow<PagingData<Content>>? = null

    fun articleListFlow(): Flow<PagingData<Content>> {
        return articleListFlow ?: ContentRepository.articleListFlow(20).cachedIn(viewModelScope).also { articleListFlow = it }
    }

}