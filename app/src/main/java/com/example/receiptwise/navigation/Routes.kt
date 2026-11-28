package com.example.receiptwise.navigation

// Screen names are kept in one place so there are no spelling mistakes
object Routes {
    const val EXPENSES = "expenses"
    const val SCAN = "scan"
    const val INSIGHTS = "insights"
    const val DETAIL = "detail/{expenseId}"

    const val ADD = "add"
    const val ADD_ROUTE = "add?shop={shop}&date={date}&amount={amount}"
}