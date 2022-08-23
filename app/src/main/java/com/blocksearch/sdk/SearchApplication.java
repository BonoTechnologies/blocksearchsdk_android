package com.blocksearch.sdk;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class SearchApplication extends MultiDexApplication {

    private static SearchApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static SearchApplication getInstance() {
        return sInstance;
    }

    public Context getActivityContext() {
        return getApplicationContext();
    }

}
