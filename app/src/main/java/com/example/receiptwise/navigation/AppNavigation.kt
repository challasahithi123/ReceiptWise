package com.example.receiptwise.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.receiptwise.screens.AddExpenseScreen
import com.example.receiptwise.screens.ExpenseDetailScreen
import com.example.receiptwise.screens.ExpensesScreen
import com.example.receiptwise.screens.InsightsSettingsScreen
import com.example.receiptwise.screens.ScanReceiptScreen

@Composable
fun AppNavigation() {
    // The controller is like a remote control that moves between screens
    val navController = rememberNavController()

    // Which screen is open right now
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // The bottom bar only shows on the three main screens (not on add or detail)
    val showBottomBar = currentRoute == Routes.EXPENSES ||
            currentRoute == Routes.SCAN ||
            currentRoute == Routes.INSIGHTS

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.EXPENSES,
                        onClick = { goToTab(navController, Routes.EXPENSES) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Expenses") },
                        label = { Text("Expenses") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.SCAN,
                        onClick = { goToTab(navController, Routes.SCAN) },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Scan receipt") },
                        label = { Text("Scan") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.INSIGHTS,
                        onClick = { goToTab(navController, Routes.INSIGHTS) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Insights and settings") },
                        label = { Text("Insights") }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.EXPENSES,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.EXPENSES) {
                ExpensesScreen(
                    // sends the id of the tapped expense to the detail screen
                    onExpenseClick = { expenseId -> navController.navigate("detail/$expenseId") },
                    onAddClick = { navController.navigate(Routes.ADD) }
                )
            }

            composable(Routes.ADD) {
                // after saving or cancelling, go back to the list
                AddExpenseScreen(onDone = { navController.popBackStack() })
            }

            composable(Routes.SCAN) { ScanReceiptScreen() }
            composable(Routes.INSIGHTS) { InsightsSettingsScreen() }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
            ) { entry ->
                // receives the id sent from the list (0 if nothing was sent)
                val expenseId = entry.arguments?.getInt("expenseId") ?: 0
                ExpenseDetailScreen(
                    expenseId = expenseId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// Used when a bottom bar button is pressed
fun goToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(Routes.EXPENSES) { saveState = true }
        launchSingleTop = true // do not open the same screen twice
        restoreState = true
    }
}