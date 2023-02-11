package com.cmj.wanandroid.network.bean

data class PageModule<T>(
    val curPage: Int,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int,
    var datas: List<T>
)


