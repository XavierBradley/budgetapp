package com.example.budgetapp.data.repository

import com.example.budgetapp.data.local.dao.BudgetDao
import com.example.budgetapp.data.local.dao.ExpenseDao
import com.example.budgetapp.data.local.dao.GoalDao
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.data.local.entity.Goal

class BudgetRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val goalDao: GoalDao
) {

    // EXPENSES
    fun getExpenses() = expenseDao.getAllExpenses()
    fun getTotalExpenses() = expenseDao.getTotalExpenses()

    suspend fun addExpense(expense: Expense) =
        expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: Expense) =
        expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: Expense) =
        expenseDao.deleteExpense(expense)

    // BUDGET
    fun getBudget() = budgetDao.getBudget()

    suspend fun setBudget(budget: Budget) =
        budgetDao.setBudget(budget)

    // GOALS
    fun getGoals() = goalDao.getGoals()

    suspend fun addGoal(goal: Goal) =
        goalDao.insertGoal(goal)

    suspend fun updateGoal(goal: Goal) =
        goalDao.updateGoal(goal)

    suspend fun deleteGoal(goal: Goal) =
        goalDao.deleteGoal(goal)
}