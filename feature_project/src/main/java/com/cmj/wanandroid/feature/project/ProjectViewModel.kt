package com.cmj.wanandroid.feature.project

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.common.kt.castAndEmit
import com.cmj.wanandroid.common.kt.doWhileSubscribed
import com.cmj.wanandroid.data.content.ContentRepository
import com.cmj.wanandroid.data.content.ContentViewModel
import com.cmj.wanandroid.lib.network.NetworkUtil
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.data.content.bean.Tree
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectViewModel(app: Application, private val savedStateHandle: SavedStateHandle) : ContentViewModel(app) {

    companion object {
        private const val CATEGORY_PROJECT_ID = "cid_project"
    }

    val projectTreeCategoryFlow: SharedFlow<Result<List<Tree>>> = MutableSharedFlow<Result<List<Tree>>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                if (it) emit(ContentRepository.projectTree())
            }
        }

    var projectCid : Int
        get() { return savedStateHandle.get<Int>(CATEGORY_PROJECT_ID) ?: -1 }
        set(value) { savedStateHandle.set(CATEGORY_PROJECT_ID, value) }

    private val projectCidFlow : StateFlow<Int> = MutableStateFlow(projectCid)
    val projectListFlow = MutableSharedFlow<PagingData<Content>>().doWhileSubscribed(viewModelScope) {
        var job : Job? = null
        projectCidFlow.collect { cid ->
            if (cid == -1) return@collect
            job?.cancel()
            job = viewModelScope.launch {
                ContentRepository.projectListFlow(cid).collect {
                    emit(it)
                }
            }
        }
    }.cachedIn(viewModelScope)

    fun submitCid(cid: Int): Job {
        return viewModelScope.launch {
            projectCidFlow.castAndEmit(cid)
        }
    }
}