package com.cmj.wanandroid.feature.search

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.common.kt.castAndEmit
import com.cmj.wanandroid.common.kt.doWhileSubscribed
import com.cmj.wanandroid.data.content.ContentRepository
import com.cmj.wanandroid.data.content.ContentViewModel
import com.cmj.wanandroid.lib.network.NetworkUtil
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.data.content.bean.Hotkey
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(app: Application) : ContentViewModel(app) {

    val hotKeyFlow: SharedFlow<Result<List<Hotkey>>> = MutableSharedFlow<Result<List<Hotkey>>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                if (it) emit(ContentRepository.hotKey())
            }
        }

    val queryKeyFlow: SharedFlow<String> = MutableSharedFlow(1)
    val queryArticleListFlow =
        MutableSharedFlow<PagingData<Content>>().doWhileSubscribed(viewModelScope) {
            var job: Job? = null
            queryKeyFlow.collect { k ->
                job?.cancel()
                job = viewModelScope.launch {
                    ContentRepository.queryArticleListFlow(k = k).collect {
                        emit(it)
                    }
                }
            }
        }.cachedIn(viewModelScope)

    fun queryKey(key: String): Job {
        return viewModelScope.launch {
            queryKeyFlow.castAndEmit(key)
        }
    }
}