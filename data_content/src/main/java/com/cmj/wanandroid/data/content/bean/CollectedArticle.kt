package com.cmj.wanandroid.data.content.bean

data class CollectedArticle(
    val id: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    val title: String,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Int,
    val publishTime: Long,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
) {
    fun toContent(): Content {
        return Content(
            originId, userId, visible, zan, title,
            null, link, niceDate, "", null, "", null,
            origin, publishTime,author, collect = false, fresh = false, top = false
        )
    }
}