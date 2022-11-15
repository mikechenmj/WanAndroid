package com.cmj.wanandroid.network.bean

import com.cmj.wanandroid.network.bean.User.CoinInfo

data class ShareArticleInfo(
    val coinInfo: CoinInfo,
    val shareArticles: PageModule<Content>
)