package com.example.receiptwise.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiptwise.data.AppDatabase
import com.example.receiptwise.data.Expense

@Composable
fun ExpenseDetailScreen(expenseId: Int, onBackClick: () -> Unit) {

    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).expenseDao() }

    // The expense starts as null and is filled in once the database answers
    var expense by remember { mutableStateOf<Expense?>(null) }
    var loaded by remember { mutableStateOf(false) }

    // Runs once when the screen opens. It uses the id to load the expense from the database.
    LaunchedEffect(expenseId) {
        expense = dao.getById(expenseId)
        loaded = true
    }

    // A copy, so Kotlin allows us to use it safely after the null check
    val item = expense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(onClick = onBackClick) { Text("< Back") }

        if (item != null) {
            Text(item.shopName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Space where the receipt photo will be shown after Sprint 3
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Receipt image will show here")
            }
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow("Date", item.date)
            DetailRow("Category", item.category)
            DetailRow("Amount", "£" + String.format("%.2f", item.amount))
        } else if (loaded) {
            // Only shown after loading finished and nothing was found
            Text("Sorry, this expense was not found.")
        }
    }
}

// One line with a label on the left and the value on the right
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(value)
    }
}