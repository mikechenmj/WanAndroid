package com.cmj.wanandroid.content.wxarticle

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.content.ContentViewModel
import com.cmj.wanandroid.kt.castAndEmit
import com.cmj.wanandroid.kt.doWhileSubscribed
import com.cmj.wanandroid.network.NetworkUtil
import com.cmj.wanandroid.network.bean.Content
import com.cmj.wanandroid.network.bean.Tree
import com.cmj.wanandroid.network.bean.WxChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WxArticleViewModel(app: Application, private val savedStateHandle: SavedStateHandle) : ContentViewModel(app) {

    companion object {
        private const val WX_OFFICIAL_ID = "wx_official_id"
    }

    val wxOfficialFlow: SharedFlow<Result<List<WxChapter>>> = MutableSharedFlow<Result<List<WxChapter>>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                if (it) emit(ContentRepository.wxOfficial())
            }
        }

    var wxId : Int
        get() { return savedStateHandle.get<Int>(WX_OFFICIAL_ID) ?: -1 }
        set(value) { savedStateHandle.set(WX_OFFICIAL_ID, value) }

    val wxIdFlow : StateFlow<Int> = MutableStateFlow(wxId)
    val wxArticleListFlow = MutableSharedFlow<PagingData<Content>>().doWhileSubscribed(viewModelScope) {
        var job : Job? = null
        wxIdFlow.collect { id ->
            job?.cancel()
            job = viewModelScope.launch {
                ContentRepository.wxArticleListFlow(id).collect {
                    emit(it)
                }
            }
        }
    }.cachedIn(viewModelScope)

    fun submitId(cid: Int): Job {
        return viewModelScope.launch {
            wxIdFlow.castAndEmit(cid)
        }
    }
}