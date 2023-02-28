package com.cmj.wanandroid.data.user.bean

data class CoinHistory (
    val id: Int,
    val userId: Int,
    val userName: String,
    val date: Long,
    val desc: String,
    val coinCount: Int,
    val reason: String,
    val type: Int,
        )