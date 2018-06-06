package com.hongfei02chen.xpwechathelper;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.hongfei02chen.xpwechathelper.hook.RevokeMsgHook;
import com.hongfei02chen.xpwechathelper.utils.AppLog;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * created by chenhongfei on 2018/5/30
 */
public class Main implements IXposedHookLoadPackage {

    private final String WECHAT_PACKAGE = "com.tencent.mm";
    private static String wechatVersion = "";
    private final String MAIN_ACTIVITY = "com.hongfei02chen.xpwechathelper.MainActivity";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        final String packageName = lpparam.packageName;
        final String processName = lpparam.processName;


        if (lpparam.appInfo == null || (lpparam.appInfo.flags & (ApplicationInfo.FLAG_SYSTEM |
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)) != 0) {
            return;
        }


        if (BuildConfig.APPLICATION_ID.equals(packageName)) {

            XposedHelpers.findAndHookMethod(MAIN_ACTIVITY,
                    lpparam.classLoader,
                    "onCreate",
                    Bundle.class,
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            //获取到当前hook的类,这里是MainActivity
                            Class clazz = param.thisObject.getClass();
                            XposedBridge.log("class name:" + clazz.getName());
                            // 输入框不为私有private 可通过以下方式获取
                            //Field field = clazz.getField("tv_text");// 密码输入框 id
                            // 通过反射获取控件，无论parivate或者public
                            Field field = clazz.getDeclaredField("tvText");
                            // 设置访问权限（这点对于有过android开发经验的可以说很熟悉）
                            field.setAccessible(true);
                            TextView textView = (TextView) field.get(param.thisObject);
                            String string = textView.getText().toString();
                            XposedBridge.log("原来的字符 : " + string);
                            // 设置属性
                            textView.setText("我是被Xposed修改的啦");
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(MAIN_ACTIVITY, lpparam.classLoader,
                    "showModuleActiveInfo", boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.args[0] = true;
                            super.beforeHookedMethod(param);
                        }
                    });


        }

        if (WECHAT_PACKAGE.equals(packageName)) {

            if (WECHAT_PACKAGE.equals(processName)) {
                // 只HOOK UI进程
                try {
                    // 由于微信Tinker的存在，hook Application.attach 不如 ContextWrapper.attachBaseContext稳定
                    // 参见 ：https://github.com/Gh0u1L5/WechatMagician/blob/master/src/main/java/com/gh0u1l5/wechatmagician/backend/WechatHook.kt
                    XposedHelpers.findAndHookMethod(ContextWrapper.class,
                            "attachBaseContext",
                            Context.class, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    Context context = (Context) param.args[0];
                                    wechatVersion = getVersionName(context, WECHAT_PACKAGE);
                                    AppLog.w("Found wechat version:" + wechatVersion + " packageName:" + packageName + " processName:" + processName);
                                    ClassLoader appClassLoader = context.getClassLoader();
                                    handleHook(appClassLoader, wechatVersion);
                                }
                            });
                } catch (Throwable e) {
                    XposedBridge.log(e);
                }
            }
        }
    }

    private void handleHook(ClassLoader classLoader, String versionName) {
//        new TencentLocationManagerHook(versionName).hook(classLoader);
//        new EmojiGameHook(versionName).hook(classLoader);
//        new MoneyHook(versionName).hook(classLoader);
//        new UIHook(versionName).hook(classLoader);
//        LauncherUIHook.getInstance().init(classLoader, versionName);
//        ExdeviceRankHook.getInstance().init(classLoader, versionName);
        RevokeMsgHook.getInstance().init(classLoader, versionName);
//        ExtDeviceWXLoginUIHook.getInstance().init(classLoader, versionName);
    }

    private String getVersionName(Context context, String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(pkgName, 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}

