package com.example.budgetapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budgetapp.data.local.dao.BudgetDao
import com.example.budgetapp.data.local.dao.ExpenseDao
import com.example.budgetapp.data.local.dao.GoalDao
import com.example.budgetapp.data.local.entity.Budget
import com.example.budgetapp.data.local.entity.Expense
import com.example.budgetapp.data.local.entity.Goal

@Database(
    entities = [Expense::class, Budget::class, Goal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}