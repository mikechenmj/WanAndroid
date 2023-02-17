package com.cmj.wanandroid.lib.network.bean

data class QAComments(
    val id: Int,
    val anonymous: Int, // 0 为匿名
    val appendForContent: Int,
    val articleId: Int,
    val canEdit: Boolean,
    val content: String,
    val contentMd: String,
    val niceDate: String,
    val publishDate: Long,
    val replyCommentId: Int,
    val replyComments: List<QAComments>,
    val rootCommentId: Int,
    val status: Int,
    val toUserId: Int,
    val toUserName: String,
    val userId: Int,
    val userName: String,
    val zan: Int,
)