package com.example.budgetapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.ui.screens.AddEditExpenseScreen
import com.example.budgetapp.ui.screens.BudgetScreen
import com.example.budgetapp.ui.screens.DashboardScreen
import com.example.budgetapp.ui.screens.ExpenseScreen
import com.example.budgetapp.ui.screens.GoalsScreen
import com.example.budgetapp.viewmodel.MainViewModel

enum class AppScreen {
    Dashboard,
    Expenses,
    AddEditExpense,
    Budget,
    Goals
}

@Composable
fun NavGraph(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf(AppScreen.Dashboard) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }

    Scaffold(
        bottomBar = {
            if (currentScreen != AppScreen.AddEditExpense) {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { screen ->
                        selectedExpense = null
                        currentScreen = screen
                    }
                )
            }
        }
    ) { paddingValues ->
        when (currentScreen) {
            AppScreen.Dashboard -> {
                DashboardScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues),
                    onViewExpensesClick = {
                        currentScreen = AppScreen.Expenses
                    },
                    onViewBudgetClick = {
                        currentScreen = AppScreen.Budget
                    },
                    onViewGoalsClick = {
                        currentScreen = AppScreen.Goals
                    }
                )
            }

            AppScreen.Expenses -> {
                ExpenseScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues),
                    onAddExpenseClick = {
                        selectedExpense = null
                        currentScreen = AppScreen.AddEditExpense
                    },
                    onEditExpenseClick = { expense ->
                        selectedExpense = expense
                        currentScreen = AppScreen.AddEditExpense
                    }
                )
            }

            AppScreen.AddEditExpense -> {
                AddEditExpenseScreen(
                    viewModel = viewModel,
                    existingExpense = selectedExpense,
                    onExpenseSaved = {
                        selectedExpense = null
                        currentScreen = AppScreen.Expenses
                    },
                    onCancelClick = {
                        selectedExpense = null
                        currentScreen = AppScreen.Expenses
                    }
                )
            }

            AppScreen.Budget -> {
                BudgetScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            AppScreen.Goals -> {
                GoalsScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == AppScreen.Dashboard,
            onClick = { onScreenSelected(AppScreen.Dashboard) },
            icon = { Text(text = "Home") },
            label = { Text(text = "Home") }
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.Expenses,
            onClick = { onScreenSelected(AppScreen.Expenses) },
            icon = { Text(text = "List") },
            label = { Text(text = "Expenses") }
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.Budget,
            onClick = { onScreenSelected(AppScreen.Budget) },
            icon = { Text(text = "Budget") },
            label = { Text(text = "Budget") }
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.Goals,
            onClick = { onScreenSelected(AppScreen.Goals) },
            icon = { Text(text = "Goals") },
            label = { Text(text = "Goals") }
        )
    }
}
