package com.example.financialapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.financialapp.MainActivityPackage.MainAccountFragment;
import com.example.financialapp.MainActivityPackage.MainBudgetsFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    public MainViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new MainBudgetsFragment();
            default:
                return new MainAccountFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
