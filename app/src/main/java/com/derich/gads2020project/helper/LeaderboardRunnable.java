package com.derich.gads2020project.helper;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.derich.gads2020project.R;
import com.derich.gads2020project.model.Leaderboard;
import com.derich.gads2020project.utility.LeaderboardUtil;
import com.derich.gads2020project.utility.SharedPreferenceUtil;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

import static com.derich.gads2020project.utility.LeaderboardUtil.API_HOURS;

public class LeaderboardRunnable implements Runnable {

    private static final String TAG = "LeaderboardRunnable";
    private WeakReference<Context> mActivityContext;
    private WeakReference<Handler> mActivityHandler;
    private String[] mPathUrls;
    private ArrayList<String> storedJSONData;


    public LeaderboardRunnable(Context context, Handler handler, String...pathUrls){
        this.mActivityContext = new WeakReference<>(context);
        this.mActivityHandler = new WeakReference<>(handler);
        this.mPathUrls = pathUrls;
    }

    public LeaderboardRunnable(Handler handler, ArrayList<String> storedJSONData,String...pathUrls){
        this.mActivityHandler = new WeakReference<>(handler);
        this.storedJSONData = storedJSONData;
        this.mPathUrls = pathUrls;
    }

    @Override
    public void run() {
        Bundle bundle;
        if (mActivityContext == null){
            bundle = getLeaderboardDataFromStorage();
        }else {
            bundle = getLeaderboardDataFromNetwork();
        }

        if (bundle == null){
            Log.d(TAG,"NO JSON RETRIEVED: Bundle is null");
            Message message = Message.obtain(mActivityHandler.get(), LeaderboardUtil.HANDLER_MESSAGE_NO_JSON_RETRIEVED);
            message.sendToTarget();
        }else {
            Log.d(TAG,"JSON RETRIEVED: Bundle is not null");
            Message message = Message.obtain(mActivityHandler.get(),LeaderboardUtil.HANDLER_MESSAGE_JSON_RETRIEVED);
            message.setData(bundle);
            message.sendToTarget();
        }
    }

    private Bundle getLeaderboardDataFromStorage(){
        Log.d(TAG,"RETRIEVING JSON FROM STORAGE");
        Bundle bundle = new Bundle();
        for (int i = 0; i < mPathUrls.length;i++) {
            try {
                final int badgeImagePlaceholder = i == API_HOURS ?
                        R.drawable.top_learner_placeholder : R.drawable.top_skill_iq_placeholder;

                ArrayList<Leaderboard> learningLeaderJSONData = LeaderboardUtil.getLearningLeaderJSONData(storedJSONData.get(i),
                        i, badgeImagePlaceholder);
                bundle.putParcelableArrayList(mPathUrls[i], learningLeaderJSONData);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Log.d(TAG, "NO JSON RETRIEVED: Error Catched");
                return null;
            }
        }
        return bundle;
    }

    private Bundle getLeaderboardDataFromNetwork(){
        Log.d(TAG,"RETRIEVING JSON FROM NETWORK");
        Bundle bundle = new Bundle();
        for (int i = 0; i < mPathUrls.length;i++) {
            try {

                URL url = LeaderboardUtil.getConnectionUrl(mPathUrls[i]);
                String jsonData = LeaderboardUtil.getJSON(url);
                SharedPreferenceUtil.storeJSON(mActivityContext.get(),mPathUrls[i],jsonData);

                if (jsonData.isEmpty())
                    return null;

                final int badgeImagePlaceholder = i == API_HOURS ?
                        R.drawable.top_learner_placeholder : R.drawable.top_skill_iq_placeholder;

                ArrayList<Leaderboard> learningLeaderJSONData = LeaderboardUtil.getLearningLeaderJSONData(jsonData, i, badgeImagePlaceholder);
                bundle.putParcelableArrayList(mPathUrls[i], learningLeaderJSONData);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                Log.d(TAG, "NO JSON RETRIEVED: Error Catched");
                return null;
            }
        }
        return bundle;
    }

}
