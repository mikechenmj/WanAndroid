package com.cmj.wanandroid.base.image

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmj.wanandroid.R

fun <T> RequestBuilder<T>.commonOption(): RequestBuilder<T> {
    return error(R.drawable.error)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
}