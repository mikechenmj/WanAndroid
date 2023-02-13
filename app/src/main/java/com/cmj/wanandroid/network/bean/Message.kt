package com.cmj.wanandroid.network.bean

data class Message(
    val id: Int,
    val userId: Int,
    val title: String,
    val message: String,
    val tag: String,
    val link: String,
    val fullLink: String,
    val date: Long,
    val niceDate: String,
    val fromUser: String,
    val fromUserId: Int,
    val category: Int,
    val isRead: Int, //0代表未读，1代表已读
) {
    companion object {
        const val UNREAD = 0
        const val READ = 1
    }
}