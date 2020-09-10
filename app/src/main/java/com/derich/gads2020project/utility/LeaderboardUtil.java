package com.derich.gads2020project.utility;

import android.net.Uri;
import android.util.Log;

import com.derich.gads2020project.model.Leaderboard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class LeaderboardUtil {
    public static final int API_HOURS = 0;
    public static final int API_SKILLIQ = 1;
    private static final String BASE_URL = "https://gadsapi.herokuapp.com";
    private static final String TAG = "LeaderboardUtil";


    private static final String LEARNING_LEADER_POINT_DESCRIPTION = " learning hours, ";
    private static final String SKILL_IQ_LEADER_POINT_DESCRIPTION = " skill IQ Score, ";

    public static final int HANDLER_MESSAGE_NO_JSON_RETRIEVED = 404;
    public static final int HANDLER_MESSAGE_JSON_RETRIEVED = 200;

    private LeaderboardUtil(){}

    public static URL getConnectionUrl(String pathUrl) throws MalformedURLException {
        Log.d(TAG,"Getting URL");
        URL fullUrl = null;

        Uri uri = Uri.parse(BASE_URL).buildUpon().path(pathUrl).build();
        fullUrl = new URL(uri.toString());
        Log.d(TAG,"Full URL" + fullUrl);
        return fullUrl;
    }

    public static String getJSON(URL url) throws IOException{
        Log.d(TAG,"Retrieving JSON Data");
        String json = null;

        final HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        if(scanner.hasNext())
            json = scanner.next();
        return json;
    }


    public static ArrayList<Leaderboard> getLearningLeaderJSONData(String json, int type, int badgePlaceholder){
        final String NAME = "name";
        final String POINTS = type == API_HOURS?"hours":"score";
        final String COUNTRY = "country";
        final String BADGE_URL = "badgeUrl";
        ArrayList<Leaderboard> leaderboards = new ArrayList<>();
        try {
            Log.d(TAG,"Retrieving Learning Leaders Data");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject learnerInfo = jsonArray.getJSONObject(i);

                String pointAndCountryConcat = concatPointAndCountry(
                        type,
                        learnerInfo.getInt(POINTS),
                        learnerInfo.getString(COUNTRY));

                Leaderboard learningLeader = new Leaderboard(
                        learnerInfo.getString(NAME),
                        pointAndCountryConcat,
                        learnerInfo.getString(BADGE_URL),badgePlaceholder
                );

                leaderboards.add(learningLeader);

            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        return leaderboards;
    }

    private static String concatPointAndCountry(int type, int point, String country) {
        StringBuilder stringBuilder = new StringBuilder();
        return type == API_HOURS? stringBuilder.append(point)
                .append(LEARNING_LEADER_POINT_DESCRIPTION)
                .append(country).append(".").toString()
                : stringBuilder.append(point).append(SKILL_IQ_LEADER_POINT_DESCRIPTION)
                .append(country).toString();

    }


}
