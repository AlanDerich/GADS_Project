package com.derich.gads2020project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.derich.gads2020project.adapters.ViewPagerAdapter;
import com.derich.gads2020project.helper.LeaderboardRunnable;
import com.derich.gads2020project.utility.LeaderboardUtil;
import com.derich.gads2020project.utility.SharedPreferenceUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity implements
        View.OnClickListener, Handler.Callback, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "LeaderboardActivity";
    public static final String TOP_LEARNERS_DATA = "com.derich.gads2020project.TOP_LEARNERS_DATA";
    private final String LEADERBOARD_DATA_BUNDLE = "com.derich.gads2020project.LEADERBOARD_DATA_BUNDLE";
    public static final String LEADERBOARD_ACTIVITY_THREAD = "LeaderboardActivityThread";
    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Handler mMyThreadHandler;
    private Handler mMainThreadHandler;
    private HandlerThread mHandlerThread;

    private Bundle mBundle;

    private String[] mPathUrls;
    private int mCurrentTabPosition;
    private boolean isDataGottenFromStorage, isDataRecent, isLeaderboardBundleFromSavedInstance;
    private LeaderboardRunnable mLeaderboardRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_leaderboard);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mProgressBar = findViewById(R.id.progressBar);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        Button submitButton = findViewById(R.id.toolbar_submit_button);
        submitButton.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        initializeDisplay();

        if (savedInstanceState != null){
            mBundle = getLeaderboardBundle(savedInstanceState);
            isLeaderboardBundleFromSavedInstance = true;
        }

        isDataGottenFromStorage = false;
        isDataRecent = false;
        //checkInternetConnection();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onSaveInstanceState");
        saveLeaderboardBundle(outState);
    }

    public void saveLeaderboardBundle(Bundle bundle){
        bundle.putBundle(LEADERBOARD_DATA_BUNDLE,mBundle);
    }

    public Bundle getLeaderboardBundle(Bundle bundle){
        return bundle.getBundle(LEADERBOARD_DATA_BUNDLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        hideNavigationBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        initializeThreads();
        if (isLeaderboardBundleFromSavedInstance){
            addBundleToFragments();
        }else {
            displayContents();
        }
    }

    private void initializeDisplay() {
        mPathUrls = getResources().getStringArray(R.array.leaderboard_categories_url);
        Log.d(TAG,"Initializing ViewAdapter");
    }

    private void initializeThreads(){
        mHandlerThread = new HandlerThread(LEADERBOARD_ACTIVITY_THREAD);
        mHandlerThread.start();
        if (mMainThreadHandler == null)
            mMainThreadHandler = new Handler(this);
        mMyThreadHandler = new Handler(mHandlerThread.getLooper());
    }
    private void displayContents() {
        if (isDataGottenFromStorage || isDataRecent)
            return;
        ArrayList<String> savedJSON = getStoredData();
        if (savedJSON != null){
            Log.d(TAG,"RETRIEVING DATA FROM STORED JSON");
            isDataGottenFromStorage = true;
            mLeaderboardRunnable = new LeaderboardRunnable(mMainThreadHandler,savedJSON,mPathUrls);
            mMyThreadHandler.post(mLeaderboardRunnable);
        }else {
            mProgressBar.setVisibility(View.VISIBLE);
            Log.d(TAG,"RETRIEVING DATA FROM INTERNET");
            isDataGottenFromStorage = false;
            getJSONDataFromInternet();
        }
    }


    //For large data, query to storage should be moved to background thread
    private ArrayList<String> getStoredData(){
        Log.d(TAG,"RETRIEVING JSON FROM STORAGE");
        ArrayList<String> savedJSONData = new ArrayList<>();
        for (String pathUrl : mPathUrls) {
            String savedJSON = SharedPreferenceUtil.getStoredJSON(this, pathUrl);
            if (savedJSON.isEmpty())
                return null;
            savedJSONData.add(savedJSON);
        }
        return savedJSONData;
    }

    private void getJSONDataFromInternet(){
        isDataRecent = true;
        mLeaderboardRunnable = new LeaderboardRunnable(this, mMainThreadHandler, mPathUrls);
        mMyThreadHandler.post(mLeaderboardRunnable);
    }

    private void checkInternetConnection() {
        //check if user internet is switched on
    }

    @Override
    public boolean handleMessage(Message msg) {
        mCurrentTabPosition = mTabLayout.getSelectedTabPosition();
        switch (msg.what){
            case LeaderboardUtil.HANDLER_MESSAGE_JSON_RETRIEVED:
                mBundle = msg.getData();
                Log.d(TAG, "JSON RETRIEVED: Handling Message");
                addBundleToFragments();
                break;
            case LeaderboardUtil.HANDLER_MESSAGE_NO_JSON_RETRIEVED:
                Log.d(TAG,"NO JSON RETRIEVED: Handling Message");
                isDataRecent = false;
                if (!isDataGottenFromStorage)
                    isDataGottenFromStorage = true;
                if (mProgressBar.getVisibility() == ProgressBar.VISIBLE)
                    mProgressBar.setVisibility(View.INVISIBLE);
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this,R.string.network_error_message,Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }

    private void addBundleToFragments(){
        Log.d(TAG,"Adding JSON Bundle to Fragment Instances");
        ArrayList<LeaderboardFragment> leaderboardFragments = new ArrayList<>();
        if (mBundle == null){
            return;
        }
        for (int i = 0; i < mPathUrls.length; i++) {
            Bundle specificBundle = new Bundle();
            specificBundle.putParcelableArrayList(mPathUrls[i], mBundle.getParcelableArrayList(mPathUrls[i]));
            specificBundle.putInt(TOP_LEARNERS_DATA,i);

            LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
            leaderboardFragment.setArguments(specificBundle);
            Log.d(TAG,"Initializing LeaderboardFragments Start: " + leaderboardFragment.getId());
            leaderboardFragments.add(leaderboardFragment);
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,leaderboardFragments);
        mViewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(mTabLayout, mViewPager, (new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                if (position == 0){
                    tab.setText(R.string.tab_item_1_text);
                }else if (position == 1){
                    tab.setText(R.string.tab_item_2_text);
                }
                /*if (position == mCurrentTabPosition)
                    tab.select();*/
            }
        })).attach();
        mTabLayout.getTabAt(mCurrentTabPosition).select();

        if (mProgressBar.getVisibility() == ProgressBar.VISIBLE)
            mProgressBar.setVisibility(View.INVISIBLE);
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        startBackgroundRetrievalOfOnlineData();
    }

    private void startBackgroundRetrievalOfOnlineData() {
        if (isDataGottenFromStorage)
            Toast.makeText(this,R.string.swipe_to_refresh_message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_submit_button){
            startActivity(new Intent(this, SubmissionActivity.class));
        }
    }

    @Override
    public void onRefresh() {
        isDataGottenFromStorage = false;
        getJSONDataFromInternet();
    }

    private void hideNavigationBar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |  View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandlerThread.quit();
        mMyThreadHandler.removeCallbacks(mLeaderboardRunnable);
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }

}