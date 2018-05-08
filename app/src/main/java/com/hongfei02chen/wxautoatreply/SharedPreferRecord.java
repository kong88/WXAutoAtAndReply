package com.hongfei02chen.wxautoatreply;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * created by chenhongfei on 2018/5/8
 */
public class SharedPreferRecord {
    private static final String FILE_NAME = "record";
    private static SharedPreferences mSharedPreferences;

    public static SharedPreferences getInstance(Context context) {
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        return mSharedPreferences;
    }


}
