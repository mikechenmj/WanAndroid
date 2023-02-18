package com.cmj.wanandroid.data.content.bean

data class WxChapter(
    val id: Int,
    val name: String,
    val courseId: Int,
    val order: Int,
    val parentChapterId: Int,
    val type: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)