package com.example.expenseses;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.*;

public class MonthlyFragment extends Fragment {

    private EditText descriptionEditText, amountEditText, budgetEditText;
    private Button addExpenseButton, setBudgetButton;
    private TextView totalTextView, budgetStatusTextView;
    private ListView expensesListView;

    private List<Expense> monthlyExpenses = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private AppDatabase db;

    private double currentBudget = 0;
    private double totalSpent = 0;

    public MonthlyFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_monthly, container, false);

        descriptionEditText = rootView.findViewById(R.id.descriptionEditText);
        amountEditText = rootView.findViewById(R.id.amountEditText);
        addExpenseButton = rootView.findViewById(R.id.addExpenseButton);
        budgetEditText = rootView.findViewById(R.id.budgetEditText);
        setBudgetButton = rootView.findViewById(R.id.setBudgetButton);
        totalTextView = rootView.findViewById(R.id.totalTextView);
        budgetStatusTextView = rootView.findViewById(R.id.budgetStatusTextView);
        expensesListView = rootView.findViewById(R.id.expensesListView);

        TextView monthTextView = rootView.findViewById(R.id.monthTextView);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String monthName = monthFormat.format(calendar.getTime());

        monthTextView.setText("Month: " + monthName);

        db = AppDatabase.getInstance(requireContext());

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<>());
        expensesListView.setAdapter(adapter);

        expensesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            Expense selectedExpense = monthlyExpenses.get(position);
            showExpenseOptionsDialog(selectedExpense);
            return true;
        });

        addExpenseButton.setOnClickListener(v -> addExpense());
        setBudgetButton.setOnClickListener(v -> setBudget());

        loadBudgetAndExpenses();

        return rootView;
    }

    private void addExpense() {
        String description = descriptionEditText.getText().toString();
        String amountStr = amountEditText.getText().toString();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(getContext(), "Enter description and amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        long timestamp = System.currentTimeMillis();

        Expense expense = new Expense(description, amount, timestamp);

        AsyncTask.execute(() -> {
            db.expenseDao().insertExpense(expense);
            requireActivity().runOnUiThread(this::loadBudgetAndExpenses);
        });

        descriptionEditText.setText("");
        amountEditText.setText("");
    }

    private void showExpenseOptionsDialog(Expense expense) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Select Option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditExpenseDialog(expense);
                    } else if (which == 1) {
                        deleteExpense(expense);
                    }
                }).show();
    }

    private void deleteExpense(Expense expense) {
        AsyncTask.execute(() -> {
            db.expenseDao().deleteExpense(expense);
            requireActivity().runOnUiThread(this::loadBudgetAndExpenses);
        });
    }

    private void showEditExpenseDialog(Expense expense) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_expense, null);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        EditText editAmount = dialogView.findViewById(R.id.editAmount);

        editDescription.setText(expense.description);
        editAmount.setText(String.valueOf(expense.amount));

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Expense")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newDescription = editDescription.getText().toString();
                    String newAmountStr = editAmount.getText().toString();

                    if (!newDescription.isEmpty() && !newAmountStr.isEmpty()) {
                        double newAmount = Double.parseDouble(newAmountStr);
                        expense.description = newDescription;
                        expense.amount = newAmount;

                        AsyncTask.execute(() -> {
                            db.expenseDao().updateExpense(expense);
                            requireActivity().runOnUiThread(this::loadBudgetAndExpenses);
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enter valid data", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    private void setBudget() {
        String budgetStr = budgetEditText.getText().toString();

        if (TextUtils.isEmpty(budgetStr)) {
            Toast.makeText(getContext(), "Enter budget amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double budgetAmount = Double.parseDouble(budgetStr);

        AsyncTask.execute(() -> {
            Budget existingBudget = db.budgetDao().getBudgetByPeriod("monthly");

            if (existingBudget == null) {
                db.budgetDao().insertBudget(new Budget("monthly", budgetAmount));
            } else {
                existingBudget.amount = budgetAmount;
                db.budgetDao().updateBudget(existingBudget);
            }

            requireActivity().runOnUiThread(() -> {
                currentBudget = budgetAmount;
                updateBudgetStatus();
                budgetEditText.setText("");
            });
        });
    }

    private void loadBudgetAndExpenses() {
        AsyncTask.execute(() -> {
            Budget budget = db.budgetDao().getBudgetByPeriod("monthly");
            List<Expense> allExpenses = db.expenseDao().getAllExpenses();

            // Filter by month
            List<Expense> filtered = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH);
            int currentYear = cal.get(Calendar.YEAR);

            for (Expense e : allExpenses) {
                cal.setTimeInMillis(e.timestamp);
                if (cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear) {
                    filtered.add(e);
                }
            }

            monthlyExpenses = filtered;
            totalSpent = 0;
            for (Expense e : monthlyExpenses) totalSpent += e.amount;

            requireActivity().runOnUiThread(() -> {
                if (budget != null) currentBudget = budget.amount;
                updateExpenseList();
                updateBudgetStatus();
            });
        });
    }

    private void updateExpenseList() {
        List<String> expenseStrings = new ArrayList<>();
        for (Expense e : monthlyExpenses) {
            expenseStrings.add(e.description + ": $" + e.amount);
        }
        adapter.clear();
        adapter.addAll(expenseStrings);
        adapter.notifyDataSetChanged();
        totalTextView.setText("Total: $" + String.format("%.2f", totalSpent));
    }

    private void updateBudgetStatus() {
        if (currentBudget == 0) {
            budgetStatusTextView.setText("No budget set");
        } else {
            double percentage = (totalSpent / currentBudget) * 100;
            budgetStatusTextView.setText("Budget: $" + currentBudget +
                    " (" + String.format("%.0f", percentage) + "% used)");
        }
    }
}

