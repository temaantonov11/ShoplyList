package com.example.shoplylist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Purchase::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun purchaseDao(): PurchaseDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "purchase_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}