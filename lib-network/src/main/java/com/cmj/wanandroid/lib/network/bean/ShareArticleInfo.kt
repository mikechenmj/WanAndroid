package com.cmj.wanandroid.lib.network.bean

import com.cmj.wanandroid.lib.network.bean.User.CoinInfo

data class ShareArticleInfo(
    val coinInfo: CoinInfo,
    val shareArticles: PageModule<Content>
)