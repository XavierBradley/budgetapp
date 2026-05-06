package com.example.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.budgetapp.data.local.AppDatabase
import com.example.budgetapp.data.repository.BudgetRepository
import com.example.budgetapp.ui.navigation.AppNavigation
import com.example.budgetapp.ui.theme.BudgetappTheme
import com.example.budgetapp.viewmodel.MainViewModel
import com.example.budgetapp.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = BudgetRepository(
            expenseDao = database.expenseDao(),
            budgetDao = database.budgetDao(),
            goalDao = database.goalDao()
        )

        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(repository)
        )[MainViewModel::class.java]

        setContent {
            BudgetappTheme {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}