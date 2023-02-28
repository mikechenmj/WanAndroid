package com.cmj.wanandroid.data.user.bean

data class User(
    val coinInfo: CoinInfo,
    val userInfo: UserInfo
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
        val collectIds: List<Int>,
    )
}