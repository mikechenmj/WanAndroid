package com.cmj.wanandroid.network

data class User(
    val id: Long,
    val username: String,
    val nickname: String,
    val coinCount: Long,
    val icon: String,
    val email: String,
//    val email1: String,
)