package com.example.receiptwise.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// This is the database itself. version = 1 is the first version of the tables.
// exportSchema = false just stops a warning message from appearing.
@Database(entities = [Expense::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao

    companion object {
        // only want ONE database in the whole app, so the copy is kept here
        @Volatile
        private var instance: AppDatabase? = null

        // Makes the database the first time and gives back the same one after that
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "receiptwise.db"
                ).build().also { instance = it }
            }
        }
    }
}