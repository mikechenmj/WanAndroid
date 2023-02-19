package com.cmj.wanandroid.data.content.bean

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