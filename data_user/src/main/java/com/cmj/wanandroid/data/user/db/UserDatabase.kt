package com.cmj.wanandroid.data.user.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cmj.wanandroid.data.user.bean.User

@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)

abstract class UserDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase =
            INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context,
                UserDatabase::class.java, "user_database")
                .enableMultiInstanceInvalidation()
                .allowMainThreadQueries()
                .build().also { INSTANCE = it }
        }
    }

    abstract fun userDao(): UserDao
}