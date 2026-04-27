package com.example.budgetapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.data.local.entity.Goal
import com.example.budgetapp.data.repository.BudgetRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    val expenses = repository.getExpenses()
    val totalExpenses = repository.getTotalExpenses()
    val budget = repository.getBudget()
    val goals = repository.getGoals()

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun setBudget(budget: Budget) {
        viewModelScope.launch {
            repository.setBudget(budget)
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            repository.addGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}