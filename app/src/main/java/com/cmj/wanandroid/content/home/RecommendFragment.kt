package com.cmj.wanandroid.content.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.cmj.wanandroid.content.AbsContentPageFragment
import com.cmj.wanandroid.network.bean.Content
import kotlinx.coroutines.flow.Flow

class RecommendFragment : AbsContentPageFragment<ViewModel, RecommendViewModel>() {

    override fun getPageFlow(): Flow<PagingData<Content>> {
        return activityViewModel.articleListFlow()
    }

    override fun showTag(): Boolean {
        return true
    }
}