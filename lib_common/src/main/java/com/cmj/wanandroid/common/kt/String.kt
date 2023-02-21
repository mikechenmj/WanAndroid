package com.cmj.wanandroid.common.kt

fun String?.nullOrValid(): String? {
    if (isNullOrBlank() || isNullOrEmpty()) return null
    return this
}