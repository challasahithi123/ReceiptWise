package com.example.receiptwise.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO means Data Access Object. It lists everything the app can ask the database to do.
// Room writes the code inside these functions for us.
@Dao
interface ExpenseDao {

    // Gets all expenses with the newest first.
    // A Flow sends a new list every time the data changes, so the screen updates by itself.
    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAll(): Flow<List<Expense>>

    // Finds one expense by its id. It gives back null if there is no match.
    // suspend means it runs in the background so the app does not freeze.
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getById(expenseId: Int): Expense?

    // Saves a new expense in the database
    @Insert
    suspend fun insert(expense: Expense)
}