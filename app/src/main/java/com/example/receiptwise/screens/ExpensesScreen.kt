package com.example.receiptwise.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiptwise.data.Expense
import com.example.receiptwise.data.SampleData

@Composable
fun ExpensesScreen(onExpenseClick: (Int) -> Unit) {

    val expenses = SampleData.expenseList

    // check if the phone is turned sideways
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Expenses",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (isLandscape) {
            // sideways: show two columns so the space is used well
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(expenses) { expense ->
                    ExpenseCard(expense = expense, onExpenseClick = onExpenseClick)
                }
            }
        } else {
            // normal: one column list
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(expenses) { expense ->
                    ExpenseCard(expense = expense, onExpenseClick = onExpenseClick)
                }
            }
        }
    }
}

// One card in the list. It shows the shop, date, category and amount.
@Composable
fun ExpenseCard(expense: Expense, onExpenseClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpenseClick(expense.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = expense.shopName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = expense.date + " - " + expense.category,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "£" + String.format("%.2f", expense.amount),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}