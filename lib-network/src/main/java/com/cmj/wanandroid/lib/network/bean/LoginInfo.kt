package com.cmj.wanandroid.lib.network.bean

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LoginInfo(
    val id: Long,
    val username: String,
    val nickname: String,
    @Json(name = "coinCount") val coin: Long,
    val icon: String,
    val email: String,
): Parcelable