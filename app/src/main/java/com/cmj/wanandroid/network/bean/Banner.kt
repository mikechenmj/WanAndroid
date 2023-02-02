package com.cmj.wanandroid.network.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Banner(
    val id: Int,
    val title: String,
    val desc: String,
    val type: Int,
    val imagePath: String,
    val url: String,
    val isVisible: Int,
    val order: Int,
): Comparable<Banner>, Parcelable {
    override fun compareTo(other: Banner): Int {
       return order - other.order
    }
}