package com.derich.gads2020project;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.derich.gads2020project.adapters.LeaderboardRecyclerViewAdapter;
import com.derich.gads2020project.model.Leaderboard;
import com.derich.gads2020project.utility.LeaderboardUtil;

import java.util.ArrayList;

public class LeaderboardFragment extends Fragment {

    private static final String TAG = "LeaderboardFragment";
    private static final String LEARNING_LEADER = "com.derich.gads2020project.LEARNING_LEADER";
    private static final String SKILLIQ_LEADER = "com.derich.gads2020project.SKILLIQ_LEADER";
    private Bundle mBundle = new Bundle();
    private String[] mPathUrls;
    private ArrayList<Leaderboard> mLeaderboards;
    private RecyclerView mRecyclerView;

    public LeaderboardFragment() {}
    // Required empty public constructor

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPathUrls = getActivity().getResources().getStringArray(R.array.leaderboard_categories_url);
        mBundle = this.getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        mRecyclerView = view.findViewById(R.id.rv_leaderboard_list);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeDisplay();
    }

    public void initializeDisplay(){
        if (mBundle != null && mPathUrls != null) {
            Log.d(TAG,"Getting Respective Leaderboard Data");
            switch (mBundle.getInt(LeaderboardActivity.TOP_LEARNERS_DATA)) {
                case LeaderboardUtil.API_HOURS:
                    mLeaderboards = mBundle.getParcelableArrayList(mPathUrls[LeaderboardUtil.API_HOURS]);
                    break;
                case LeaderboardUtil.API_SKILLIQ:
                    mLeaderboards = mBundle.getParcelableArrayList(mPathUrls[LeaderboardUtil.API_SKILLIQ]);
            }
            if (mLeaderboards != null) {
                Log.d(TAG,"Setting up RecyclerView");
                LeaderboardRecyclerViewAdapter recyclerViewAdapter = new LeaderboardRecyclerViewAdapter(getActivity(), mLeaderboards);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(recyclerViewAdapter);
            }
        }else Log.d(TAG,"Bundle is null");
    }
}