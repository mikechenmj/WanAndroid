package com.cmj.wanandroid.content

import android.app.Application
import com.cmj.wanandroid.BaseViewModel
import com.cmj.wanandroid.lib.network.bean.Content

open class ContentViewModel(app: Application) : BaseViewModel(app) {

    suspend fun star(content: Content) = ContentRepository.star(content)
    suspend fun unStar(content: Content) = ContentRepository.unStar(content)
}