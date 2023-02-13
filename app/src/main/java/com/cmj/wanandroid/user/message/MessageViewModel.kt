package com.cmj.wanandroid.user.message

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.network.bean.Message
import com.cmj.wanandroid.user.UserRepository
import kotlinx.coroutines.flow.Flow

class MessageViewModel(app: Application) : BaseViewModel(app) {

    private var messageReadListFlow: Flow<PagingData<Message>>? = null
    fun messageReadListFlow(): Flow<PagingData<Message>> {
        return messageReadListFlow ?: UserRepository.messageReadListFlow()
            .cachedIn(viewModelScope)
            .also { messageReadListFlow = it }
    }

    private var messageUnReadListFlow: Flow<PagingData<Message>>? = null
    fun messageUnReadListFlow(): Flow<PagingData<Message>> {
        return messageUnReadListFlow ?: UserRepository.messageUnReadListFlow()
            .cachedIn(viewModelScope)
            .also { messageUnReadListFlow = it }
    }
}