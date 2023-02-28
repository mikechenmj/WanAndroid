package com.cmj.wanandroid.data.content.bean

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Banner(
    @PrimaryKey val id: Int,
    @NonNull val title: String,
    @NonNull val desc: String,
    @NonNull val type: Int,
    @NonNull val imagePath: String,
    @NonNull val url: String,
    @NonNull val isVisible: Int,
    @NonNull val order: Int,
) : Comparable<Banner>, Parcelable {
    override fun compareTo(other: Banner): Int {
        return order - other.order
    }
}