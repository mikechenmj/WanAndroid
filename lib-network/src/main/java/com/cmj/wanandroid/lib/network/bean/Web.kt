package com.cmj.wanandroid.lib.network.bean

data class Web(
    val id: Int,
    val userId: Int,
    val name: String,
    val desc: String,
    val icon: String,
    val link: String,
    val order: Int,
    val visible: Int
)