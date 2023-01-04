package com.cmj.wanandroid.kt

fun String?.nullOrValid(): String? {
    if (isNullOrBlank() || isNullOrEmpty()) return null
    return this
}