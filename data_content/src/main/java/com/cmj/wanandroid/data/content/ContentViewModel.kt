package com.cmj.wanandroid.data.content

import android.app.Application
import com.cmj.wanandroid.data.content.bean.Content
import com.cmj.wanandroid.lib.base.BaseViewModel

open class ContentViewModel(app: Application) : BaseViewModel(app) {

    suspend fun star(content: Content) = ContentRepository.star(content)
    suspend fun unStar(content: Content) = ContentRepository.unStar(content)
}