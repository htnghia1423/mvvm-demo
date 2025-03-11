package com.example.mvvmdemo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mvvmdemo.BuildConfig
import com.example.mvvmdemo.data.dao.ItemDao
import com.example.mvvmdemo.data.dao.UserDao
import com.example.mvvmdemo.data.model.Item
import com.example.mvvmdemo.data.model.User
import net.sqlcipher.database.SupportFactory

@Database(entities = [Item::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = BuildConfig.DB_PASSPHRASE
                val factory = SupportFactory(passphrase.toByteArray())
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .openHelperFactory(factory)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}