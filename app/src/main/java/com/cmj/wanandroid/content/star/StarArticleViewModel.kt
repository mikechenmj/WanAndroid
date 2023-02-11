package com.cmj.wanandroid.content.star

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.content.ContentViewModel
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class StarArticleViewModel(app: Application) : ContentViewModel(app) {

    private var starArticleListFlow: Flow<PagingData<Content>>? = null

    fun starArticleListFlow(): Flow<PagingData<Content>> {
        return starArticleListFlow ?: ContentRepository.starArticleListFlow(20)
            .cachedIn(viewModelScope).also { starArticleListFlow = it }
    }
}