package com.cmj.wanandroid.lib.base.image

import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cmj.wanandroid.lib.base.R

fun <T> RequestBuilder<T>.commonOption(): RequestBuilder<T> {
    return error(R.drawable.error)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(false)
}