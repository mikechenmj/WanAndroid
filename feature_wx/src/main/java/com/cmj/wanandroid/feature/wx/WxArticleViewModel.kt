package com.cmj.wanandroid.feature.wx

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
import com.cmj.wanandroid.data.content.bean.WxChapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
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

    private val wxIdFlow : StateFlow<Int> = MutableStateFlow(wxId)
    val wxArticleListFlow = MutableSharedFlow<PagingData<Content>>().doWhileSubscribed(viewModelScope) {
        var job : Job? = null
        wxIdFlow.collect { id ->
            if (wxId == -1) return@collect
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