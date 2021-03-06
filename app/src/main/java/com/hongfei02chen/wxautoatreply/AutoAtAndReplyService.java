package com.hongfei02chen.wxautoatreply;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by chenhongfei on 2018/5/7
 */
public class AutoAtAndReplyService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReplyService";

    private final static String MM_PNAME = "com.tencent.mm";
    boolean hasAction = false;
    boolean locked = false;
    boolean background = false;
    private String name;
    private String scontent;
    AccessibilityNodeInfo itemNodeinfo;
    private KeyguardManager.KeyguardLock kl;
    private Handler handler = new Handler();

    private final String RESOURCE_ID_TEXT = "com.tencent.mm:id/jz";
    private final String RESOURCE_ID_BUTTON = "com.tencent.mm:id/aag";
    private final String RESOURCE_ID_IMAGEVIEW = "com.tencent.mm:id/jx";
    private final String OWNER_IMAGE_DESC = "kaka头像";

    private static final String REGEX_1 = "^\"([^\"]+?)\"邀请\"([^\"]+?)\"";
    private static final String REGEX_2 = "^\"([^\"]+?)\"通过扫描";
    private static final String REGEX_AT = "^@([^@,]+?), 我是自动回复";
    public static final String REGEX_3 = "邀请\"([^\"]+?)\"加入了群聊.*";

    private static final String SEND_CONTENT = "Hello![太阳] 欢迎新店主入群\n" +
            "\n" +
            "[闪电]重点看这里 \n" +
            "\n" +
            "[闪电]扫码下面二维码\n" +
            "[闪电]激活店主权限\n" +
            "\n" +
            "\n" +
            "【[啤酒]每日特价39元！】——温碧泉蚕丝兔斯基面膜\n" +
            "\n" +
            "[机智]分享卖出可赚15.6元\n" +
            "[礼物] 自购最多可省25.6元（首单立减10元+佣金15.6元）\n" +
            "\n" +
            "新店主操作手册\n" +
            "https://shimo.im/docs/Yghjb1Y2hZ8ee0o5/\n";

    /**
     * 记录用户加入群聊的次数
     */
    private Map<String, Integer> mJoinNumMap = new HashMap<>();

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.d(TAG, "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findNickname(RESOURCE_ID_TEXT);
                    }
                }, 1000);


                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                Log.d(TAG, "get notification event");
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (!TextUtils.isEmpty(content)) {
                            if (isScreenLocked()) {
                                locked = true;
                                wakeAndUnlock();
                                if (isAppForeground(MM_PNAME)) {
                                    background = false;
                                    Log.d(TAG, "mm is locked and  in foreground");
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendNotifacationReply(event);
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    Log.d(TAG, "mm is locked and  in background");
                                    sendNotifacationReply(event);
                                }
                            } else {
                                locked = false;
                                if (isAppForeground(MM_PNAME)) {
                                    background = false;
                                    Log.d(TAG, "mm is unlocked and in foreground");
                                    sendNotifacationReply(event);
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (fill()) {
                                                send();
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    background = true;
                                    Log.d(TAG, "mm is unlocked and in background");
                                    sendNotifacationReply(event);
                                }
                            }
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    //resource-id : com.tencent.mm:id/jz
    private void findNickname(String clickId) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(clickId);
        if (list == null) {
            return;
        }
        for (AccessibilityNodeInfo nodeInfo : list) {
            if ("android.widget.TextView".equals(nodeInfo.getClassName())) {
//                Log.i(TAG, "================== text: " + nodeInfo.getText());

                if (nodeInfo.getText() == null || TextUtils.isEmpty(nodeInfo.getText().toString())) {
                    continue;
                }
                String content = nodeInfo.getText().toString();

                String matcherString = regexMatcher(content);
                if (TextUtils.isEmpty(matcherString)) {
                    continue;
                }

                // 判断at 加入之后，是否曾经at过
                Rect screenRect = new Rect();
                nodeInfo.getBoundsInScreen(screenRect);
//                Log.i(TAG, "================== content: " + content +  " screenRect.bottom:" + screenRect.bottom);
                if (isEverAt(rootNode, screenRect.bottom)) {
                    continue;
                }

                checkAndFillAndSend(matcherString);
            }
        }

    }

    protected boolean isEverAt(AccessibilityNodeInfo rootNode, int textBottom) {
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(RESOURCE_ID_IMAGEVIEW);
        if (list == null) {
            return false;
        }
        int bottom = 0;
        for (AccessibilityNodeInfo nodeInfo : list) {
            if ("android.widget.ImageView".equals(nodeInfo.getClassName())) {
                if (TextUtils.isEmpty(nodeInfo.getContentDescription()) || !nodeInfo.getContentDescription().toString().equalsIgnoreCase(OWNER_IMAGE_DESC)) {
                    continue;
                }


                Rect screenRect = new Rect();
                nodeInfo.getBoundsInScreen(screenRect);
                Log.i(TAG, "================== getContentDescription " + nodeInfo.getContentDescription() + " screenRect.bottom:" + screenRect.bottom);
                if (screenRect.bottom > bottom) {
                    bottom = screenRect.bottom;
                }
            }
        }
        Log.i(TAG, "================== imageView HeaderBottom: " + bottom + " textBottom: " + textBottom);
        if (textBottom > bottom) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 提取匹配的内容
     *
     * @param string
     * @return
     */
    protected String regexMatcher(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        boolean flag = false;
        Pattern pattern1 = Pattern.compile(REGEX_1);
        Pattern pattern2 = Pattern.compile(REGEX_2);


        Matcher matcher1 = pattern1.matcher(string);
        Matcher matcher2 = pattern2.matcher(string);

        Pattern pattern3 = Pattern.compile(REGEX_3);
        Matcher matcher3 = pattern3.matcher(string);
        while (matcher3.find()) {
            if (matcher3.groupCount() >= 1) {
                String matcherString = matcher3.group(1);
                if (!TextUtils.isEmpty(matcherString)) {
                    flag = true;
                    String[] nicknameArray = matcherString.split("、");
                    if (null != nicknameArray) {
                        StringBuilder atNickname = new StringBuilder();
                        for (int i = 0; i < nicknameArray.length; i++) {
                            atNickname.append("@" + nicknameArray[i] + " ");
                        }
                        Log.e(TAG, "find 33333 atNickname:" + atNickname.toString() + " sourceString:" + string);
                        return atNickname.toString();
                    }
                }
            }
        }

        if (flag) {
            return "";
        }
        while (matcher1.find()) {
            if (matcher1.groupCount() == 2) {
                flag = true;
                String atNickname = matcher1.group(2);
                Log.e(TAG, "find 11111 atNickname:" + atNickname + " sourceString:" + string);
                return atNickname;
            }
        }
        if (flag) {
            return "";
        }
        while (matcher2.find()) {
            if (matcher2.groupCount() >= 1) {
                flag = true;
                String atNickname = matcher2.group(1);
                Log.e(TAG, "find 2222222 atNickname:" + atNickname + " sourceString:" + string);
                return atNickname;
            }
        }

        return "";
    }

    private void checkAndFillAndSend(String nickname) {
        fillAndSend(SEND_CONTENT, nickname);
    }

    private void fillAndSend(String string, String nickname) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
//        Log.d(TAG, " ===============String:" + string);
        boolean flag = findEditText(rootNode, nickname);
//        Log.d(TAG, " ===============flag:" + flag);
        if (flag) {
            send();
        }
        flag = findEditText(rootNode, string);
        if (flag) {
            send();
        }
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        int count = rootNode.getChildCount();

//        Log.d(TAG, "root class=" + rootNode.getClassName() + "," + rootNode.getText() + "," + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo == null) {
                Log.d(TAG, "nodeinfo = null");
                continue;
            }

//            Log.d(TAG, "class=" + nodeInfo.getClassName());

            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                Log.i(TAG, "================== EditText : " + content);
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                ClipData clip = ClipData.newPlainText("label", content);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }
//
            if (findEditText(nodeInfo, content)) {
                return true;
            }
        }

        return false;
    }

    private void sendNotifacationReply(AccessibilityEvent event) {
        hasAction = true;
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event
                    .getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");
            name = cc[0].trim();
            scontent = cc[1].trim();

            Log.i(TAG, "sender name =" + name + " sender content =" + scontent);

            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private boolean fill() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findEditText(rootNode, "正在忙,稍后回复你");
        }
        return false;
    }

    /**
     * 寻找窗体中的“发送”按钮，并且点击。
     */
    private void send() {
        send("");
    }

    private void send(String nickname) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(RESOURCE_ID_BUTTON);
        if (list == null) {
            return;
        }
        for (AccessibilityNodeInfo node : list) {
            if ("android.widget.Button".equals(node.getClassName()) && node.isEnabled()) {
                Log.i(TAG, "================== click send ");
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * 系统是否在锁屏状态
     *
     * @return
     */
    private boolean isScreenLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    private void wakeAndUnlock() {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }

    private void release() {

        if (locked && kl != null) {
            Log.d(TAG, "release the lock");
            //得到键盘锁管理器对象
            kl.reenableKeyguard();
            locked = false;
        }
    }

    private boolean isAppForeground(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }
}
