package com.example.expenseses;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class BudgetPagerAdapter extends FragmentStateAdapter {

    public BudgetPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0: return new WeeklyFragment();
            case 1: return new MonthlyFragment();
            case 2: return new YearlyFragment();
            default: return new WeeklyFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
