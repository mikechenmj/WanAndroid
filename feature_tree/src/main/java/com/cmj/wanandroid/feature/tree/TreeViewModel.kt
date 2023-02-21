package com.cmj.wanandroid.feature.tree

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.data.content.ContentRepository
import com.cmj.wanandroid.data.content.ContentViewModel
import com.cmj.wanandroid.common.kt.castAndEmit
import com.cmj.wanandroid.common.kt.doWhileSubscribed
import com.cmj.wanandroid.lib.network.NetworkUtil
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.data.content.bean.Tree
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TreeViewModel(app: Application, private val savedStateHandle: SavedStateHandle) : ContentViewModel(app) {

    companion object {
        private const val CATEGORY_ID_FIRST = "cid_first"
        private const val CATEGORY_ID_SECOND = "cid_second"
        private const val TRANSLATE_X_FIRST = "translate_x_first"
    }

    val treeCategoryFlow: SharedFlow<Result<List<Tree>>> = MutableSharedFlow<Result<List<Tree>>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                if (it) emit(ContentRepository.tree())
            }
        }

    var cidFirst : Int
        get() { return savedStateHandle.get<Int>(CATEGORY_ID_FIRST) ?: -1 }
        set(value) { savedStateHandle.set(CATEGORY_ID_FIRST, value) }

    var cidSecond : Int
        get() { return savedStateHandle.get<Int>(CATEGORY_ID_SECOND) ?: -1 }
        set(value) { savedStateHandle.set(CATEGORY_ID_SECOND, value) }

    var firstScrollX : Int
        get() { return savedStateHandle.get<Int>(TRANSLATE_X_FIRST) ?: 0 }
        set(value) { savedStateHandle.set(TRANSLATE_X_FIRST, value) }

    private val articleCidFlow : StateFlow<Int> = MutableStateFlow(cidSecond)
    val cidArticleListFlow = MutableSharedFlow<PagingData<Content>>().doWhileSubscribed(viewModelScope) {
        var job : Job? = null
        articleCidFlow.collect { cid ->
            if (cid == -1) return@collect
            job?.cancel()
            job = viewModelScope.launch {
                ContentRepository.articleListWithIdFlow(cid).collect {
                    emit(it)
                }
            }
        }
    }.cachedIn(viewModelScope)

    fun submitCid(cid: Int): Job {
        return viewModelScope.launch {
            articleCidFlow.castAndEmit(cid)
        }
    }
}