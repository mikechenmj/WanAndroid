package com.cmj.wanandroid.kt

inline fun <K, reified V> Map<out K, V>.toArray(): Array<V?> {
    val array = arrayOfNulls<V>(size)
    var index = 0
    forEach {
        array[index] = it.value
        index++
    }
    return array
}