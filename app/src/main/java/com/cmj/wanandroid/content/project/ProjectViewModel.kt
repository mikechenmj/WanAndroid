package com.cmj.wanandroid.content.project

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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