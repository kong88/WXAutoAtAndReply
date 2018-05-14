package com.hongfei02chen.wxautoatreply;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * created by chenhongfei on 2018/5/14
 */
public class MyApplication extends Application{
    private static final OkHttpClient mHttpClient = new OkHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static OkHttpClient getmHttpClient() {
        return mHttpClient;
    }
}
