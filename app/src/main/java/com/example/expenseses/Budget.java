package com.example.expenseses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String period; // "weekly", "monthly", "yearly"
    public double amount;

    public Budget(String period, double amount) {
        this.period = period;
        this.amount = amount;
    }
}

