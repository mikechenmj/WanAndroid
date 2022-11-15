package com.cmj.wanandroid.network.bean

data class Todo(
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val completeDateStr: String,
    val completeDate: Long?,
    val status: Int,
    val priority: Int,
    val date: Long,
    val dateStr: String,
    val type: Int,
)