package com.cmj.wanandroid.data.content.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cmj.wanandroid.data.content.bean.Banner

@Dao
interface BannerDao {

    @Query("SELECT * FROM Banner")
    suspend fun banners(): Array<Banner>

    @Query("SELECT * FROM Banner WHERE id = :id")
    suspend fun query(id: Int): Banner?

    @Insert
    suspend fun insertSuspend(vararg banners: Banner): List<Long>

    @Update
    suspend fun updateSuspend(vararg banner: Banner): Int

    @Delete
    suspend fun deleteSuspend(vararg banners: Banner): Int
}