package com.example.receiptwise.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiptwise.data.AppDatabase
import com.example.receiptwise.data.Expense
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// This screen is the form to check, change and save an expense.
// The start values come from the scan. They are empty when the user adds an expense by hand.
@Composable
fun AddExpenseScreen(
    startShop: String,
    startDate: String,
    startAmount: String,
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getInstance(context).expenseDao() }
    val scope = rememberCoroutineScope()

    // Today's date is used when the scan did not find a date
    val today = SimpleDateFormat("dd MMM yyyy", Locale.UK).format(Date())

    // These hold what is in the form. They begin with the values from the scan.
    var shopName by remember { mutableStateOf(startShop) }
    var date by remember { mutableStateOf(if (startDate.isBlank()) today else startDate) }
    var amountText by remember { mutableStateOf(startAmount) }
    var category by remember { mutableStateOf("Food") }
    var error by remember { mutableStateOf("") }

    val categories = listOf("Food", "Travel", "Bills", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Check Expense", fontSize = 26.sp, fontWeight = FontWeight.Bold)

        // take() cuts the text to a maximum length so very long text cannot be typed
        OutlinedTextField(
            value = shopName,
            onValueChange = { shopName = it.take(30) },
            label = { Text("Shop name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = date,
            onValueChange = { date = it.take(15) },
            label = { Text("Date") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.take(9) },
            label = { Text("Amount in pounds") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Category")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { name ->
                FilterChip(
                    selected = category == name,
                    onClick = { category = name },
                    label = { Text(name) }
                )
            }
        }

        // Only shows when something is wrong with the form
        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull()

                // Check the form before saving, so wrong data never reaches the database
                if (shopName.isBlank() || date.isBlank()) {
                    error = "Please fill in the shop name and the date."
                } else if (amount == null || amount <= 0) {
                    error = "Please enter an amount greater than zero."
                } else {
                    scope.launch {
                        dao.insert(Expense(shopName = shopName.trim(), date = date.trim(), amount = amount, category = category))
                        onDone() // go back to the list after saving
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
        }

        TextButton(onClick = onDone) {
            Text("Cancel")
        }
    }
}