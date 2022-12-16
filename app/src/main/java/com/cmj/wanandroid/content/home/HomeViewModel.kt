package com.cmj.wanandroid.content.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.content.ContentRepository
import com.cmj.wanandroid.kt.DEFAULT_SHARED_FLOW_STOP_TIMEOUT_MILLIS
import com.cmj.wanandroid.kt.castAndEmit
import com.cmj.wanandroid.kt.doWhileSubscribed
import com.cmj.wanandroid.network.NetworkUtil
import com.cmj.wanandroid.network.bean.Banner
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

class HomeViewModel(app: Application) : BaseViewModel(app) {

    val bannerFlow: SharedFlow<Result<List<Banner>>> = MutableSharedFlow<Result<List<Banner>>>(1)
        .doWhileSubscribed(viewModelScope) {
            NetworkUtil.networkConnectedStateFlow.collectLatest {
                if (it) emit(ContentRepository.banner())
            }
        }

    suspend fun refreshBanner() = bannerFlow.castAndEmit(ContentRepository.banner())
}