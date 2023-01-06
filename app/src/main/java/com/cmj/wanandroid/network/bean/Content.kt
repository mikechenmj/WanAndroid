package com.cmj.wanandroid.network.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    val id: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    val title: String,
    val type: Int,
    val link: String,
    val niceDate: String,
    val niceShareDate: String,
    val shareDate: Long?,
    val shareUser: String,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<Tag>,
    val realSuperChapterId: Int,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val selfVisible: Int,
    val adminAdd: Boolean,
    val isAdminAdd: Boolean,
    val apkLink: String,
    val audit: Int,
    val author: String,
    val canEdit: Boolean,
    val chapterId: Int,
    val chapterName: String,
    var collect: Boolean,
    val courseId: Int,
    val desc: String,
    val descMd: String,
    val envelopePic: String,
    val fresh: Boolean,
    val host: String,
    @Transient var top: Boolean = false
) : Parcelable {

    @Parcelize
    data class Tag(
        val name: String,
        val url: String,
    ) : Parcelable
}