package com.example.receiptwise.data

// For now I am using made up expenses.
// In Sprint 2 this will be replaced by a real database.
object SampleData {

    val expenseList = listOf(
        Expense(1, "Tesco", "12 Nov 2026", 14.50, "Food"),
        Expense(2, "Northern Rail", "11 Nov 2026", 22.80, "Travel"),
        Expense(3, "British Gas", "10 Nov 2026", 61.00, "Bills"),
        Expense(4, "Costa Coffee", "09 Nov 2026", 4.35, "Food"),
        Expense(5, "WHSmith", "08 Nov 2026", 9.99, "Shopping"),
        Expense(6, "Boots", "07 Nov 2026", 12.40, "Health")
    )

    // This looks for an expense with the id we give it.
    // It gives back null if it cannot find one.
    fun findExpenseById(id: Int): Expense? {
        for (expense in expenseList) {
            if (expense.id == id) {
                return expense
            }
        }
        return null
    }
}