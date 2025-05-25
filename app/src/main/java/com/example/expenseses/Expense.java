package com.example.expenseses;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String description;
    public double amount;
    public long timestamp; // store as milliseconds

    public Expense(String description, double amount, long timestamp) {
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
    }
}

