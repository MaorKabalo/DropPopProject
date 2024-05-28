package com.example.droppopproject.fragments;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter class for managing fragments within a ViewPager2 for tab navigation.
 * This adapter provides fragments for different tabs in the application.
 */
public class TabItemFragmentAdapter extends FragmentStateAdapter {

    /**
     * Constructor for the TabItemFragmentAdapter.
     *
     * @param f The FragmentActivity that will host this adapter.
     */
    public TabItemFragmentAdapter(FragmentActivity f) {
        super(f);
    }

    /**
     * Called to instantiate the fragment for the given position.
     *
     * @param position The position of the fragment in the ViewPager.
     * @return The new fragment instance.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SettingsFragment();
            case 1:
                return new PlayFragment();
            default:
                return new LeaderboardFragment();
        }
    }

    /**
     * Returns the number of items to be displayed in the ViewPager.
     *
     * @return The total number of fragments.
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}
