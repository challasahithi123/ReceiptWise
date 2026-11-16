package com.example.receiptwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.receiptwise.navigation.AppNavigation
import com.example.receiptwise.ui.theme.ReceiptWiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReceiptWiseTheme {
                AppNavigation()
            }
        }
    }
}