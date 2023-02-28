package com.cmj.wanandroid.data.user.db

import androidx.room.*
import com.cmj.wanandroid.data.user.bean.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    suspend fun query(): User?

    @Update
    suspend fun updateSuspend(user: User): Int

    @Insert
    suspend fun insertSuspend(user: User): Int

    @Delete
    suspend fun deleteSuspend(): Int
}