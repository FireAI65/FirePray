package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DoaBookmarkEntity::class], version = 1, exportSchema = false)
abstract class DoaDatabase : RoomDatabase() {
    abstract fun doaBookmarkDao(): DoaBookmarkDao

    companion object {
        @Volatile
        private var INSTANCE: DoaDatabase? = null

        fun getDatabase(context: Context): DoaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoaDatabase::class.java,
                    "doa_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
