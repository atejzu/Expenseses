package com.example.expenseses;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Expense.class, Budget.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ExpenseDao expenseDao();
    public abstract BudgetDao budgetDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "expense_db")
                    .fallbackToDestructiveMigration() // for dev, reset on schema change
                    .build();
        }
        return INSTANCE;
    }
}

