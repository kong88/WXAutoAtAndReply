package com.hongfei02chen.xpwechathelper.hook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;
import com.hongfei02chen.xpwechathelper.utils.Constant;
import com.hongfei02chen.xpwechathelper.utils.DataUtils;
import com.hongfei02chen.xpwechathelper.utils.MessageUtils;
import com.hongfei02chen.xpwechathelper.utils.PropertiesUtils;
import com.hongfei02chen.xpwechathelper.utils.XmlUtils;
import com.hongfei02chen.xpwechathelper.utils.XposedLog;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by su on 2018/1/29.
 */

public class RevokeMsgHook {

    private static Map<Long, Object> msgCacheMap = new HashMap<>();
    private static Object storageInsertClazz;

    private String insertClassName;
    private String insertMethodName;

    private static boolean disableRevoke;
    private ClassLoader classLoader;

    private static String SQLiteDatabaseClassName = "com.tencent.wcdb.database.SQLiteDatabase";

    private RevokeMsgHook() {

    }

    public static RevokeMsgHook getInstance() {
        return RevokeMsgHook.ExdeviceRankHookHolder.instance;
    }

    private static class ExdeviceRankHookHolder {
        @SuppressLint("StaticFieldLeak")
        private static final RevokeMsgHook instance = new RevokeMsgHook();
    }

    public void init(Context context, ClassLoader classLoader, String versionName) {
        insertClassName = "com.tencent.mm.storage.av";
        insertMethodName = "b";
        if (versionName.startsWith("6.6.6")) {
            insertClassName = "com.tencent.mm.storage.ba";
            insertMethodName = "b";
        }
        if (this.classLoader == null) {
            this.classLoader = classLoader;
            hook(context, classLoader);
        }
    }

    private void hook(final Context context, ClassLoader classLoader) {
        try {
            Class clazz = XposedHelpers.findClass(SQLiteDatabaseClassName, classLoader);
            XposedHelpers.findAndHookMethod(clazz, "updateWithOnConflict",
                    String.class, ContentValues.class, String.class, String[].class, int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (param.args[0].equals("message")) {
                                ContentValues contentValues = ((ContentValues) param.args[1]);
                                XposedLog.d("===contentValues:" + contentValues);

//                                if (disableRevoke && null != contentValues && contentValues.getAsInteger("type") == 10000 &&
//                                        !contentValues.getAsString("content").equals("你撤回了一条消息")) {
//                                    handleMessageRecall(contentValues);
//                                    param.setResult(1);
//                                }
                            }

                            super.beforeHookedMethod(param);
                        }
                    });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }

        try {
            Class clazz = XposedHelpers.findClass(SQLiteDatabaseClassName, classLoader);
            XposedHelpers.findAndHookMethod(clazz, "delete",
                    String.class, String.class, String[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String[] media = {"ImgInfo2", "voiceinfo", "videoinfo2", "WxFileIndex2"};
                            if (disableRevoke && Arrays.asList(media).contains(param.args[0])) {
                                param.setResult(1);
                            }
                            super.beforeHookedMethod(param);
                        }
                    });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }

        try {
            XposedHelpers.findAndHookMethod(File.class, "delete",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String path = ((File) param.thisObject).getAbsolutePath();
                            if (disableRevoke &&
                                    (path.contains("/image2/") || path.contains("/voice2/") || path.contains("/video/")))
                                param.setResult(true);
                            super.beforeHookedMethod(param);
                        }
                    });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }

        try {
            // insert method
            Class clazz = XposedHelpers.findClass(insertClassName, classLoader);
            XposedBridge.hookAllMethods(clazz, insertMethodName,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            storageInsertClazz = param.thisObject;
                            AppLog.w("afterHookedMethod storageInsertClazz:" + storageInsertClazz);
                            Object msg = param.args[0];
                            long msgId = -1;
                            try {
                                msgId = XposedHelpers.getLongField(msg, "field_msgId");
                                msgCacheMap.put(msgId, msg);
                                AppLog.w("msgId:" + msgId + " msg:" + msg);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                            super.afterHookedMethod(param);
                        }

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            AppLog.w("beforeHookedMethod  param:" + param.thisObject);
                            super.beforeHookedMethod(param);
                        }
                    });
        } catch (Error | Exception e) {
            e.printStackTrace();
        }

        try {
            XposedHelpers.findAndHookMethod(SQLiteDatabaseClassName, classLoader, "insert", String.class, String.class, ContentValues.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        ContentValues contentValues = (ContentValues) param.args[2];
                        String tableName = (String) param.args[0];
                        AppLog.w("auto123 table:" + tableName + " param1:" + param.args[1] + " contentValues:" + contentValues.toString());
                        if (TextUtils.isEmpty(tableName) || !tableName.equals("message")) {
                            return;
                        }
                        handlerJoinMessage(context, contentValues);

//                        Integer type = contentValues.getAsInteger("type");
//                        if (null == type) {
//                            return;
//                        }
//                        if (type == 436207665 || type == 469762097) {
//                            handleLuckyMoney(contentValues, lpparam);
//                        } else if (type == 419430449) {
//                            handleTransfer(contentValues, lpparam);
//                        }
                    } catch (Error | Exception e) {
                    }
                }

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    AppLog.w("auto123 beforeHookedMethod table:" + param.args[0]);
                    super.beforeHookedMethod(param);
                }
            });
        } catch (Exception e) {

        }
    }

    private static void reload() {
        disableRevoke = Boolean.valueOf(PropertiesUtils.getValue(Constant.PRO_FILE, "disable_revoke", "true"));
    }

    private static void handleMessageRecall(ContentValues contentValues) {
        long msgId = contentValues.getAsLong("msgId");
        Object msg = msgCacheMap.get(msgId);

        AppLog.w("map size:" + msgCacheMap.size() + " msg:" + msg);

        long createTime = XposedHelpers.getLongField(msg, "field_createTime");
        XposedHelpers.setIntField(msg, "field_type", contentValues.getAsInteger("type"));
        XposedHelpers.setObjectField(msg, "field_content",
                contentValues.getAsString("content") + "(已被阻止)");
        XposedHelpers.setLongField(msg, "field_createTime", createTime + 1L);
        String logContent = String.format("content1:%s, content2:%s", contentValues.getAsString("content"), XposedHelpers.getObjectField(msg, "field_content"));
        AppLog.d(logContent);
        XposedHelpers.callMethod(storageInsertClazz, "b", msg, false);

    }

    private static void handlerJoinMessage(Context context, ContentValues cv) {
        try {
            Set<String> keySet = cv.keySet();
            String key = "";
            for (String s : keySet) {
                key += s + ", ";
            }
            XposedLog.d("keySet:" + key);
            Long createTime = cv.getAsLong("createTime");
            String talker = cv.getAsString("talker");
            String content = cv.getAsString("content");
            Long type = cv.getAsLong("type");
            int talkId = cv.getAsInteger("talkerId");
            int msgId = cv.getAsInteger("msgId");

            // 判断是否为激活群组
            String roomName = MessageUtils.parseActiveGroup(content);
            XposedLog.d("===roomName:" + roomName);
            if (!TextUtils.isEmpty(roomName)) {
                DataUtils.insertChatRoom(context, talker, roomName);
                return;
            }

            // 判断是否为加群消息
            Map<String, Object> retMap = XmlUtils.parseXml(content);
            if (retMap == null) {
                return;
            }
            String template = (String) retMap.get("template");
            List<String> nicknameList = (List<String>) retMap.get("nicknameList");
            List<String> usernameList = (List<String>) retMap.get("usernameList");
            if (CollectionUtils.isEmpty(nicknameList)) {
                return;
            }
            DataUtils.enQueue(context, msgId, talker, template, nicknameList, usernameList, createTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
}
