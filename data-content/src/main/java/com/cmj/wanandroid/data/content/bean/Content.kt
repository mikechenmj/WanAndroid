package com.cmj.wanandroid.data.content.bean

import android.os.Parcelable
import android.text.Html
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    val id: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int,
    val title: String,
    val type: Int?,
    val link: String,
    val niceDate: String,
    val niceShareDate: String = "",
    val shareDate: Long?,
    val shareUser: String = "",
    val tags: List<Tag>?,
    val origin: String,
    val publishTime: Long,
    val author: String,
    var collect: Boolean = false,
    var fresh: Boolean = false,
    @Transient var top: Boolean = false
) : Parcelable {

    fun validAuthor(): String {
        return Html.fromHtml(author.ifBlank { shareUser }).toString()
    }

    @Parcelize
    data class Tag(
        val name: String,
        val url: String,
    ) : Parcelable
}