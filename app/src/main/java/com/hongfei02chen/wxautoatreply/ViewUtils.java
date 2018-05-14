package com.hongfei02chen.wxautoatreply;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by chenhongfei on 2018/5/11
 */
public class ViewUtils {
    private static final String TAG = "ViewUtils";

    private static final String REGEX_1 = "^\"([^\"]+?)\"邀请\"([^\"]+?)\"";
    private static final String REGEX_2 = "^\"([^\"]+?)\"通过扫描";
    public static final String REGEX_3 = "邀请\"([^\"]+?)\"加入了.*";
    private static final String REGEX_AT = "^@([^@,]+?), 我是自动回复";


    public static final String RESOURCE_CLASS_LL = "android.widget.LinearLayout";
    public static final String RESOURCE_CLASS_TV = "android.widget.TextView";

    // item
    public static final String RESOURCE_ID_ITEM = "com.tencent.mm:id/apt";
    // 群名字，nickname等title
    public static final String RESOURCE_ID_TITLE = "com.tencent.mm:id/apv";
    // 消息的正文内容
    public static final String RESOURCE_ID_CONTENT = "com.tencent.mm:id/apx";


    public static   List<String>  mGroupList = new ArrayList<String>(){{
        add("木头人");
    }};

    public static String getNodeText(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.getText() == null || TextUtils.isEmpty(nodeInfo.getText().toString())) {
            return  "";
        }

        return nodeInfo.getText().toString();
    }


    /**
     * 提取匹配的内容
     *
     * @param string
     * @return
     */
    public static String regexMatcherNickname(String string) {
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

    public boolean findEditText(AccessibilityService service, AccessibilityNodeInfo rootNode, String content) {
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
                ClipboardManager clipboardManager = (ClipboardManager) service.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }
//
            if (findEditText(service, nodeInfo, content)) {
                return true;
            }
        }

        return false;
    }

}
