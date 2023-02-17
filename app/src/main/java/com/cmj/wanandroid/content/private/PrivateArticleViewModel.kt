package com.cmj.wanandroid.content.private

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.content.ContentViewModel
import com.cmj.wanandroid.lib.base.bean.Content
import kotlinx.coroutines.flow.Flow

class PrivateArticleViewModel(app: Application) : ContentViewModel(app) {

    private var privateArticleListFlow: Flow<PagingData<Content>>? = null

    fun privateArticleListFlow(): Flow<PagingData<Content>> {
        return privateArticleListFlow ?: ContentRepository.privateArticleListFlow(20)
            .cachedIn(viewModelScope).also { privateArticleListFlow = it }
    }

    suspend fun deletePrivateArticle(content: Content) =
        ContentRepository.deletePrivateArticle(content)

}