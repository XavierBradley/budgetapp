package com.example.budgetapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.budgetapp.data.local.entity.Goal
import com.example.budgetapp.viewmodel.MainViewModel

@Composable
fun GoalsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.goals.collectAsState(initial = emptyList())

    var goalTitle by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var savedAmount by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

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
                Column {
                    Text(
                        text = "Savings Goals",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Create goals and track your progress.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Add Goal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = goalTitle,
                            onValueChange = {
                                goalTitle = it
                                errorMessage = ""
                            },
                            label = { Text(text = "Goal title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetAmount,
                            onValueChange = {
                                targetAmount = it
                                errorMessage = ""
                            },
                            label = { Text(text = "Target amount") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        OutlinedTextField(
                            value = savedAmount,
                            onValueChange = {
                                savedAmount = it
                                errorMessage = ""
                            },
                            label = { Text(text = "Current saved amount") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        if (errorMessage.isNotBlank()) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Button(
                            onClick = {
                                val targetValue = targetAmount.toDoubleOrNull()
                                val savedValue = savedAmount.toDoubleOrNull() ?: 0.0

                                errorMessage = when {
                                    goalTitle.isBlank() -> "Please enter a goal title."
                                    targetValue == null -> "Please enter a valid target amount."
                                    targetValue <= 0.0 -> "Target amount must be greater than 0."
                                    savedValue < 0.0 -> "Saved amount cannot be negative."
                                    savedValue > targetValue -> "Saved amount cannot be greater than the target."
                                    else -> ""
                                }

                                if (errorMessage.isBlank()) {
                                    viewModel.addGoal(
                                        Goal(
                                            title = goalTitle.trim(),
                                            targetAmount = targetValue ?: 0.0,
                                            savedAmount = savedValue
                                        )
                                    )

                                    goalTitle = ""
                                    targetAmount = ""
                                    savedAmount = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Save Goal")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Your Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (goals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No goals yet. Add a savings goal above.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(goals) { goal ->
                    GoalListItem(
                        goal = goal,
                        onAddTenClick = {
                            val updatedSavedAmount = (goal.savedAmount + 10.0).coerceAtMost(goal.targetAmount)
                            viewModel.updateGoal(
                                goal.copy(savedAmount = updatedSavedAmount)
                            )
                        },
                        onDeleteClick = {
                            viewModel.deleteGoal(goal)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalListItem(
    goal: Goal,
    onAddTenClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = if (goal.targetAmount > 0.0) {
        (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    } else {
        0f
    }

    val percent = (progress * 100).toInt()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$percent%",
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "$${String.format("%.2f", goal.savedAmount)} saved of $${String.format("%.2f", goal.targetAmount)}"
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            if (percent >= 100) {
                Text(
                    text = "Milestone reached! Goal completed.",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAddTenClick,
                    modifier = Modifier.weight(1f),
                    enabled = percent < 100
                ) {
                    Text(text = "+$10")
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }
}
