package com.cmj.wanandroid.network

import com.squareup.moshi.Json

data class User(
    val id: Long,
    val username: String,
    val nickname: String,
    @Json(name = "coinCount") val coin: Long,
    val icon: String,
    val email: String,
)