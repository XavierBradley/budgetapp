package com.example.budgetapp.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.data.local.entity.Goal
import com.example.budgetapp.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onViewExpensesClick: () -> Unit = {},
    onViewBudgetClick: () -> Unit = {},
    onViewGoalsClick: () -> Unit = {}
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)
    val budget by viewModel.budget.collectAsState(initial = null)
    val goals by viewModel.goals.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection()
            }

            item {
                SummarySection(
                    totalExpenses = totalExpenses,
                    budget = budget
                )
            }

            item {
                FinancialTipCard()
            }

            item {
                QuickActionsSection(
                    onViewExpensesClick = onViewExpensesClick,
                    onViewBudgetClick = onViewBudgetClick,
                    onViewGoalsClick = onViewGoalsClick
                )
            }

            item {
                SectionTitle(title = "Recent Expenses")
            }

            if (expenses.isEmpty()) {
                item {
                    EmptyMessage(message = "No expenses yet. Go to Expenses to add one.")
                }
            } else {
                items(expenses.take(5)) { expense ->
                    ExpenseRow(expense = expense)
                }
            }

            item {
                SectionTitle(title = "Savings Goals")
            }

            if (goals.isEmpty()) {
                item {
                    EmptyMessage(message = "No goals yet. Go to Goals to add one.")
                }
            } else {
                items(goals.take(3)) { goal ->
                    GoalRow(goal = goal)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column {
        Text(
            text = "Smart Student Budgeting",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Track spending, budgets, and savings goals.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SummarySection(
    totalExpenses: Double,
    budget: Budget?
) {
    val budgetLimit = budget?.limit ?: 0.0
    val remaining = budgetLimit - totalExpenses
    val progress = if (budgetLimit > 0) {
        (totalExpenses / budgetLimit).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val percentUsed = if (budgetLimit > 0) {
        ((totalExpenses / budgetLimit) * 100).toInt()
    } else {
        0
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Dashboard Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(text = "Total spent: $${String.format("%.2f", totalExpenses)}")

            if (budget == null) {
                Text(text = "Budget: Not set")
            } else {
                Text(text = "Budget: $${String.format("%.2f", budget.limit)} (${budget.period})")
                Text(text = "Remaining: $${String.format("%.2f", remaining)}")
                Text(text = "Used: $percentUsed%")

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )

                if (percentUsed >= 80) {
                    Text(
                        text = "Budget alert: You have used at least 80% of your budget.",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FinancialTipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Student Budget Tip",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Try reviewing small daily purchases first. Food, coffee, and transport often add up quickly."
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onViewExpensesClick: () -> Unit,
    onViewBudgetClick: () -> Unit,
    onViewGoalsClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onViewExpensesClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Expenses")
            }

            OutlinedButton(
                onClick = onViewBudgetClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Budget")
            }
        }

        OutlinedButton(
            onClick = onViewGoalsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Goals")
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ExpenseRow(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = expense.title,
                    fontWeight = FontWeight.Bold
                )
                Text(text = expense.category)
            }

            Text(
                text = "$${String.format("%.2f", expense.amount)}",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GoalRow(goal: Goal) {
    val progress = if (goal.targetAmount > 0) {
        (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = goal.title,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$${String.format("%.2f", goal.savedAmount)} saved of $${String.format("%.2f", goal.targetAmount)}"
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptyMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp)
        )
    }
}
