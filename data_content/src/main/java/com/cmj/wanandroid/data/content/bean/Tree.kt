package com.cmj.wanandroid.data.content.bean

data class Tree(
    val id: Int,
    val courseId: Int,
    val name: String,
    val author: String,
    val desc: String,
    val lisense: String,
    val lisenseLink: String,
    val cover: String,
    val children: List<Tree>,
    val order: Int,
    val type: Int,
    val userControlSetTop: Boolean,
    val parentChapterId: Int,
    val visible: Int,
)
