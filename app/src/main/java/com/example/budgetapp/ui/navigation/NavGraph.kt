package com.example.budgetapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.data.local.entity.Goal
import com.example.budgetapp.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Dashboard", "Expenses", "Budget", "Goals")

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Text(
                text = "Personal Budget Manager",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> DashboardScreen(viewModel)
                1 -> ExpensesScreen(viewModel)
                2 -> BudgetScreen(viewModel)
                3 -> GoalsScreen(viewModel)
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)
    val budget by viewModel.budget.collectAsState(initial = null)
    val goals by viewModel.goals.collectAsState(initial = emptyList())

    val budgetLimit = budget?.limit ?: 0.0
    val remaining = budgetLimit - totalExpenses

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            InfoCard("Total Expenses", money(totalExpenses))
        }

        item {
            InfoCard("Budget Remaining", money(remaining))
        }

        item {
            InfoCard("Saved Goals", goals.size.toString())
        }

        item {
            Text(
                text = "Recent Expenses",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(expenses.take(5)) { expense ->
            ExpenseCard(
                expense = expense,
                onDelete = { viewModel.deleteExpense(expense) }
            )
        }
    }
}

@Composable
fun ExpensesScreen(viewModel: MainViewModel) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Add Expense",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense title") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull()

                    if (title.isNotBlank() && amountValue != null && category.isNotBlank()) {
                        viewModel.addExpense(
                            Expense(
                                title = title,
                                amount = amountValue,
                                category = category,
                                date = System.currentTimeMillis()
                            )
                        )

                        title = ""
                        amount = ""
                        category = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Expense")
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(
                text = "All Expenses",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(expenses) { expense ->
            ExpenseCard(
                expense = expense,
                onDelete = { viewModel.deleteExpense(expense) }
            )
        }
    }
}

@Composable
fun BudgetScreen(viewModel: MainViewModel) {
    val budget by viewModel.budget.collectAsState(initial = null)
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)

    var limit by remember { mutableStateOf("") }

    val currentLimit = budget?.limit ?: 0.0
    val remaining = currentLimit - totalExpenses

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Budget Settings",
            style = MaterialTheme.typography.titleLarge
        )

        InfoCard("Current Budget", money(currentLimit))
        InfoCard("Total Spent", money(totalExpenses))
        InfoCard("Remaining", money(remaining))

        OutlinedTextField(
            value = limit,
            onValueChange = { limit = it },
            label = { Text("New budget limit") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val limitValue = limit.toDoubleOrNull()

                if (limitValue != null) {
                    viewModel.setBudget(
                        Budget(
                            id = 1,
                            limit = limitValue,
                            period = "MONTHLY"
                        )
                    )

                    limit = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Budget")
        }
    }
}

@Composable
fun GoalsScreen(viewModel: MainViewModel) {
    val goals by viewModel.goals.collectAsState(initial = emptyList())

    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Savings Goals",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal title") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = {
                    val target = targetAmount.toDoubleOrNull()

                    if (title.isNotBlank() && target != null) {
                        viewModel.addGoal(
                            Goal(
                                title = title,
                                targetAmount = target,
                                savedAmount = 0.0
                            )
                        )

                        title = ""
                        targetAmount = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Goal")
            }
        }

        item {
            HorizontalDivider()
        }

        items(goals) { goal ->
            GoalCard(
                goal = goal,
                onAddSavings = {
                    viewModel.updateGoal(
                        goal.copy(savedAmount = goal.savedAmount + 10.0)
                    )
                },
                onDelete = {
                    viewModel.deleteGoal(goal)
                }
            )
        }
    }
}

@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = expense.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Amount: ${money(expense.amount)}")
            Text("Category: ${expense.category}")
            Text("Date: ${formatDate(expense.date)}")

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onAddSavings: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) {
        goal.savedAmount / goal.targetAmount
    } else {
        0.0
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text("Saved: ${money(goal.savedAmount)}")
            Text("Target: ${money(goal.targetAmount)}")
            Text("Progress: ${(progress * 100).toInt()}%")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddSavings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ $10")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

fun money(amount: Double): String {
    return "$" + String.format(Locale.getDefault(), "%.2f", amount)
}

fun formatDate(time: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(time))
}