package com.example.receiptwise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// One expense. The @Entity line tells Room to make a table called "expenses"
// and every variable below becomes a column in that table.
@Entity(tableName = "expenses")
data class Expense(
    // Room makes the id by itself, so a new expense starts with 0
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shopName: String,
    val date: String,
    val amount: Double,
    val category: String
)