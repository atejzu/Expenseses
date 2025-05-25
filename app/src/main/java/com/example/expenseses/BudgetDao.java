package com.example.expenseses;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    void insertBudget(Budget budget);

    @Query("SELECT * FROM budgets WHERE period = :period LIMIT 1")
    Budget getBudgetByPeriod(String period);

    @Update
    void updateBudget(Budget budget);
}

