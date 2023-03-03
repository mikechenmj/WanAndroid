package com.cmj.wanandroid.data.user.db

import androidx.room.*
import com.cmj.wanandroid.data.user.bean.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE id = :id")
    suspend fun query(id: Int): User?

    @Update
    suspend fun updateSuspend(user: User): Int

    @Insert
    suspend fun insertSuspend(user: User): Long

    @Delete
    suspend fun deleteSuspend(user: User): Int
}