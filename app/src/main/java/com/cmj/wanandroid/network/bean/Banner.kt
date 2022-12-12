package com.cmj.wanandroid.network.bean

data class Banner(
    val id: Int,
    val title: String,
    val desc: String,
    val type: Int,
    val imagePath: String,
    val url: String,
    val isVisible: Int,
    val order: Int,
): Comparable<Banner> {
    override fun compareTo(other: Banner): Int {
       return order - other.order
    }
}