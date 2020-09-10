package com.derich.gads2020project.helper;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubmitProjectRunnable implements Runnable {

    private static final String SUBMISSION_LINK_BASE_URL = " https://docs.google.com/forms/d/e/";

    private static final String TAG = "SubmitProjectRunnable";
    private final int PROJECT_SUBMISSION_SUCCESSFUL = 200;
    private final int PROJECT_SUBMISSION_FAILED = 404;
    final private WeakReference<Handler> mActivityHandler;
    private String mFirstName;
    private String mLastName;
    private String mEmailAddress;
    private String mGithubLink;

    public SubmitProjectRunnable(Handler activtyHandler,String firstName, String lastName, String emailAddress, String githubLink) {
        mActivityHandler = new WeakReference<>(activtyHandler);
        mFirstName = firstName;
        mLastName = lastName;
        mEmailAddress = emailAddress;
        mGithubLink = githubLink;
    }

    @Override
    public void run() {
        submitProjectUsingRetrofit();
        //done
    }

    private void submitProjectUsingRetrofit() {
        Retrofit mRetrofit = new Retrofit.Builder().
                baseUrl(SUBMISSION_LINK_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();

        LeaderboardRetrofitInterface retrofitInterface = mRetrofit.create(LeaderboardRetrofitInterface.class);
        Call<ResponseBody> submitProjectCall = retrofitInterface.submitProject(mFirstName,mLastName,mEmailAddress,mGithubLink);
        submitProjectCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG,"Waiting for RetrofitResponse");
                if (response.isSuccessful()){
                    Log.d(TAG,"RetrofitResponse" + response.code());
                    Message message = Message.obtain(mActivityHandler.get(),PROJECT_SUBMISSION_SUCCESSFUL);
                    message.sendToTarget();
                }
                else {
                    Log.d(TAG,"RetrofitResponseError: "+response.code()+ response.errorBody());
                    Message message = Message.obtain(mActivityHandler.get(),PROJECT_SUBMISSION_FAILED);
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,"SUBMISSION FAILED: " + t.getMessage());
                Log.e(TAG,"SUBMISSION FAILED: " + t.getCause());
                Message message = Message.obtain(mActivityHandler.get(),PROJECT_SUBMISSION_FAILED);
                message.sendToTarget();

            }
        });
    }
}
