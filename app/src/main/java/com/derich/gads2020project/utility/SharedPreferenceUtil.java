package com.derich.gads2020project.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

    private static final String LEADERBOARD_SHARED_PREFERENCES = "com.derich.gads2020project.LEADERBOARD_SHAREDPREFERENCES";
    private static final String NO_SHARED_PREFERENCES = "";
    private SharedPreferenceUtil(){}
    public static SharedPreferences getPreference(Context context){
        return context.getSharedPreferences(LEADERBOARD_SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }

    public static String getStoredJSON(Context context, String key){
        String json = getPreference(context).getString(key,NO_SHARED_PREFERENCES);
        return json;
    }

    public static void storeJSON(Context context, String key, String json){
       SharedPreferences.Editor editor = getPreference(context).edit();
       editor.putString(key,json);
       editor.apply();
    }
}
