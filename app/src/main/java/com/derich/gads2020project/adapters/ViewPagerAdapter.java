package com.derich.gads2020project.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.derich.gads2020project.LeaderboardFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<LeaderboardFragment> mLeaderboardFragments = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<LeaderboardFragment> leaderboardFragments) {
        super(fragmentActivity);
        mLeaderboardFragments = leaderboardFragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mLeaderboardFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mLeaderboardFragments.size();
    }
}
