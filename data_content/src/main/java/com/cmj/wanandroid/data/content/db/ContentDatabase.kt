package com.cmj.wanandroid.data.content.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cmj.wanandroid.data.content.bean.Banner

@Database(
    entities = [Banner::class],
    version = 1,
    exportSchema = false
)

abstract class ContentDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: ContentDatabase? = null

        fun getInstance(context: Context): ContentDatabase =
            INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context,
                ContentDatabase::class.java, "content_database")
                .enableMultiInstanceInvalidation()
                .allowMainThreadQueries()
                .build().also { INSTANCE = it }
        }
    }

    abstract fun bannerDao(): BannerDao
}