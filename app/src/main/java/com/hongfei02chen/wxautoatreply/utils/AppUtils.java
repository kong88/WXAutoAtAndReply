package com.hongfei02chen.wxautoatreply.utils;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * created by chenhongfei on 2018/5/14
 */
public class AppUtils {
    private static final String TAG = "AppUtils";

    private void wakeAndUnlock(PowerManager pm, KeyguardManager km) {
        //获取电源管理器对象

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象

        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }


    public static boolean isAppForeground(ActivityManager am, String packageName) {

        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        Log.d("AutoAtAndReply", "currentPackageName:" + currentPackageName + " packageName:" + packageName);
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }

    public static boolean isAppForeground(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : appProcesses) {
            if (process.processName.equals(packageName)) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i("AutoAtAndReply", process.processName + " : " + packageName);
                if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于前台"
                            + process.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于后台" + process.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isAccessibilitySettingOn(Context context, String serviceString) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
            e.printStackTrace();
        }
        Log.d(TAG, "serviceString:" + serviceString + " accessibilityEnabled:" + accessibilityEnabled);
        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next();
                    Log.d(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + serviceString);
                    if (serviceString.equalsIgnoreCase(accessibilityService)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
