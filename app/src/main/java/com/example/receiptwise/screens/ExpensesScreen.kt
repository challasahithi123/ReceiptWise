package com.example.receiptwise.screens

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiptwise.data.AppDatabase
import com.example.receiptwise.data.Expense

@Composable
fun ExpensesScreen(onExpenseClick: (Int) -> Unit, onAddClick: () -> Unit) {

    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).expenseDao() }

    // Gets the list from the database. collectAsState makes the screen update by itself
    // whenever an expense is added, so no refresh button is needed.
    val expenses by remember { dao.getAll() }.collectAsState(initial = emptyList())

    // Check if the phone is turned sideways
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("My Expenses", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (expenses.isEmpty()) {
                // Shown when the database has nothing saved yet
                Text("No expenses yet. Tap Add expense to save your first one.")
            } else {
                // One grid for both modes: 1 column in portrait and 2 columns in landscape
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isLandscape) 2 else 1),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // space so the button does not cover the last card
                ) {
                    items(expenses) { expense ->
                        ExpenseCard(expense = expense, onExpenseClick = onExpenseClick)
                    }
                }
            }
        }

        // Button in the bottom right corner that opens the add screen
        ExtendedFloatingActionButton(
            onClick = onAddClick,
            icon = { Icon(Icons.Default.Add, contentDescription = "Add expense") },
            text = { Text("Add expense") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }
}

// One card in the list. It shows the shop, date, category and amount.
@Composable
fun ExpenseCard(expense: Expense, onExpenseClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpenseClick(expense.id) } // sends the id when the card is tapped
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(expense.shopName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(expense.date + " - " + expense.category, fontSize = 14.sp)
            }
            // %.2f always shows two decimal places, so 14.5 becomes 14.50
            Text("£" + String.format("%.2f", expense.amount), fontSize = 18.sp)
        }
    }
}