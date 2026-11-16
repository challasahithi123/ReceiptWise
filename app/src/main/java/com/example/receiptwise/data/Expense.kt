package com.example.receiptwise.data

// This class holds the information of one expense.
data class Expense(
    val id: Int,
    val shopName: String,
    val date: String,
    val amount: Double,
    val category: String
)