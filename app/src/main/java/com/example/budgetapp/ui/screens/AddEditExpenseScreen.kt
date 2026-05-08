package com.example.budgetapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    viewModel: MainViewModel,
    existingExpense: Expense? = null,
    onExpenseSaved: () -> Unit,
    onCancelClick: () -> Unit
) {
    var title by remember(existingExpense) {
        mutableStateOf(existingExpense?.title ?: "")
    }

    var amount by remember(existingExpense) {
        mutableStateOf(existingExpense?.amount?.toString() ?: "")
    }

    var selectedCategory by remember(existingExpense) {
        mutableStateOf(existingExpense?.category ?: "Food")
    }

    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "Food",
        "Transport",
        "Bills",
        "Entertainment",
        "School",
        "Other"
    )

    val screenTitle = if (existingExpense == null) "Add Expense" else "Edit Expense"
    val buttonText = if (existingExpense == null) "Save Expense" else "Update Expense"

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = screenTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enter your expense details below.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            errorMessage = ""
                        },
                        label = { Text(text = "Expense title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            errorMessage = ""
                        },
                        label = { Text(text = "Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(text = category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        onClick = {
                            val amountValue = amount.toDoubleOrNull()

                            errorMessage = when {
                                title.isBlank() -> "Please enter an expense title."
                                amountValue == null -> "Please enter a valid amount."
                                amountValue <= 0.0 -> "Amount must be greater than 0."
                                else -> ""
                            }

                            if (errorMessage.isBlank()) {
                                if (existingExpense == null) {
                                    viewModel.addExpense(
                                        Expense(
                                            title = title.trim(),
                                            amount = amountValue ?: 0.0,
                                            category = selectedCategory,
                                            date = System.currentTimeMillis()
                                        )
                                    )
                                } else {
                                    viewModel.updateExpense(
                                        existingExpense.copy(
                                            title = title.trim(),
                                            amount = amountValue ?: 0.0,
                                            category = selectedCategory
                                        )
                                    )
                                }

                                onExpenseSaved()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = buttonText)
                    }

                    OutlinedButton(
                        onClick = onCancelClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Cancel")
                    }
                }
            }
        }
    }
}
