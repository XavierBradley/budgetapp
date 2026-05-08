package com.example.budgetapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.viewmodel.MainViewModel

@Composable
fun BudgetScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val budget by viewModel.budget.collectAsState(initial = null)
    val totalExpenses by viewModel.totalExpenses.collectAsState(initial = 0.0)

    var budgetAmount by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf("MONTHLY") }
    var errorMessage by remember { mutableStateOf("") }

    val currentLimit = budget?.limit ?: 0.0
    val remaining = currentLimit - totalExpenses
    val progress = if (currentLimit > 0.0) {
        (totalExpenses / currentLimit).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val percentUsed = if (currentLimit > 0.0) {
        ((totalExpenses / currentLimit) * 100).toInt()
    } else {
        0
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Budget",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Set a weekly or monthly spending limit.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Current Budget",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (budget == null) {
                        Text(text = "No budget has been set yet.")
                    } else {
                        Text(text = "Limit: $${String.format("%.2f", currentLimit)}")
                        Text(text = "Period: ${budget?.period}")
                        Text(text = "Spent: $${String.format("%.2f", totalExpenses)}")
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Set Budget",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = budgetAmount,
                        onValueChange = {
                            budgetAmount = it
                            errorMessage = ""
                        },
                        label = { Text(text = "Budget amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PeriodButton(
                            text = "Weekly",
                            isSelected = selectedPeriod == "WEEKLY",
                            onClick = { selectedPeriod = "WEEKLY" },
                            modifier = Modifier.weight(1f)
                        )

                        PeriodButton(
                            text = "Monthly",
                            isSelected = selectedPeriod == "MONTHLY",
                            onClick = { selectedPeriod = "MONTHLY" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            val amountValue = budgetAmount.toDoubleOrNull()

                            errorMessage = when {
                                amountValue == null -> "Please enter a valid budget amount."
                                amountValue <= 0.0 -> "Budget amount must be greater than 0."
                                else -> ""
                            }

                            if (errorMessage.isBlank()) {
                                viewModel.setBudget(
                                    Budget(
                                        id = 1,
                                        limit = amountValue ?: 0.0,
                                        period = selectedPeriod
                                    )
                                )

                                budgetAmount = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Save Budget")
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier
        ) {
            Text(text = text)
        }
    }
}
