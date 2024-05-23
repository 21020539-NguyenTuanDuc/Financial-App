package com.example.financialapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.financialapp.MainActivityFragments.MainAccountFragment;
import com.example.financialapp.MainActivityFragments.MainBudgetsFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {
    public MainViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return new MainBudgetsFragment();
            default:
                return new MainAccountFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 1:
                title = "Budgets and Goals";
                break;
            default:
                title = "Account";
                break;
        }
        return title;
    }
}
