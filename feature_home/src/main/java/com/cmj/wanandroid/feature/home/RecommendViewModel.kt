package com.cmj.wanandroid.feature.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.data.content.ContentRepository
import com.cmj.wanandroid.data.content.ContentViewModel
import com.cmj.wanandroid.data.content.bean.Content
import kotlinx.coroutines.flow.Flow

class RecommendViewModel(application: Application) : ContentViewModel(application) {

    private var articleListFlow: Flow<PagingData<Content>>? = null

    fun articleListFlow(): Flow<PagingData<Content>> {
        return articleListFlow ?: ContentRepository.articleListFlow(20).cachedIn(viewModelScope).also { articleListFlow = it }
    }

}