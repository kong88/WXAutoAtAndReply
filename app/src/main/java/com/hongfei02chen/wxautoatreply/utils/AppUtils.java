package com.hongfei02chen.wxautoatreply.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.os.PowerManager;
import android.text.TextUtils;

/**
 * created by chenhongfei on 2018/5/14
 */
public class AppUtils {


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
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }
}
