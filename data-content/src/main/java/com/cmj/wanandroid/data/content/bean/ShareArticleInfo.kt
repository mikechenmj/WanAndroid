package com.cmj.wanandroid.data.content.bean

import com.cmj.wanandroid.lib.base.bean.User.CoinInfo
import com.cmj.wanandroid.lib.network.bean.PageModule

data class ShareArticleInfo(
    val coinInfo: CoinInfo,
    val shareArticles: PageModule<Content>
)