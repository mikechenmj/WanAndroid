package com.cmj.wanandroid.data.user.bean

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