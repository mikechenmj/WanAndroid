package com.cmj.wanandroid.data.user.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.squareup.moshi.Moshi

@Entity
@TypeConverters(CoinInfoConverters::class, UserInfoConverters::class)
data class User(
    val coinInfo: CoinInfo,
    val userInfo: UserInfo,
    @PrimaryKey val id: Int = userInfo.id
) {

    data class CoinInfo(
        val userId: Int,
        val username: String,
        val nickname: String,
        val rank: Int,
        val coinCount: Int,
        val level: Int,
    )

    data class UserInfo(
        val id: Int,
        val username: String,
        val nickname: String,
        val publicName: String,
        val email: String,
        val icon: String,
        val password: String,
        val token: String,
        val type: Int,
        val admin: Boolean,
        val coinCount: Int,
    )
}

abstract class Converters<T>(clazz: Class<T>) {

    private val moshi = Moshi.Builder().build().adapter(clazz)

    @TypeConverter
    fun fromJson(json: String?): T? {
        return moshi.fromJson(json ?: return null)
    }

    @TypeConverter
    fun toJson(obj: T): String {
        return moshi.toJson(obj)
    }
}

class CoinInfoConverters : Converters<User.CoinInfo>(User.CoinInfo::class.java)
class UserInfoConverters : Converters<User.UserInfo>(User.UserInfo::class.java)