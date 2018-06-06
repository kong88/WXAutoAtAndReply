package com.hongfei02chen.xpwechathelper.utils;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

/**
 * created by chenhongfei on 2018/6/1
 */
public class AppLog {

    public static final String TAG = "xposed";



    public static void d(String msg) {
        XposedBridge.log(msg);
        Log.d(TAG, msg);
    }

    public static void i(String msg) {
        XposedBridge.log(msg);
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        XposedBridge.log(msg);
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        XposedBridge.log(msg);
        Log.d(TAG, msg);
    }
}
