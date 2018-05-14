package com.hongfei02chen.wxautoatreply;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hongfei02chen.wxautoatreply.utils.CollectionUtils;
import com.hongfei02chen.wxautoatreply.utils.ViewUtils;

import java.io.IOException;
import java.util.List;

/**
 * 进入聊天页面，自动发消息
 * <p>
 * created by chenhongfei on 2018/5/7
 */
public class SendService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReply";


    boolean background = false;
    private String mGroup;
    private String mNickname;

    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();


    @Override
    public void onCreate() {
        super.onCreate();
        mSendHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.d(TAG, "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                        fillAndSend(rootNode, mGroup, mNickname);
                    }
                });

                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    //resource-id : com.tencent.mm:id/jz
    public void findGroupAndSend(AccessibilityNodeInfo rootNode, String viewId, String groupName, String nickname) {
        if (rootNode == null) {
            return;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        if (list == null) {
            return;
        }
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (ViewUtils.RESOURCE_CLASS_LL.equals(nodeInfo.getClassName())) {

                // 从nodeInfo 查找子view
                List<AccessibilityNodeInfo> titleList = nodeInfo.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_TITLE);
                List<AccessibilityNodeInfo> contentList = nodeInfo.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_CONTENT);

                if (CollectionUtils.isEmpty(titleList) || CollectionUtils.isEmpty(contentList)) {
                    continue;
                }

                // 每个nodeInfo节点只有一个title和content元素
                String title = ViewUtils.getNodeText(titleList.get(0));
                String content = ViewUtils.getNodeText(contentList.get(0));

                Log.i(TAG, String.format("=================title:%s, content:%s", title, content));
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    continue;
                }

                // 不包含这个群组
                if (!title.equals(groupName)) {
                    continue;
                }

                Log.d(TAG, String.format("====group:%s, nickname:%s", title, nickname));

                // 模拟点击，进入聊天窗口
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }


    private void fillAndSend(AccessibilityNodeInfo rootNode, String string, String nickname) {

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
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_BUTTON);
        if (list == null) {
            return;
        }
        for (AccessibilityNodeInfo node : list) {
            if ("android.widget.Button".equals(node.getClassName()) && node.isEnabled()) {
                Log.i(TAG, "================== click send ");
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
        ViewUtils.pressBackButton();
    }


    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            findGroupAndSend(rootNode, ViewUtils.RESOURCE_ID_ITEM, mGroup, mNickname);
            mSendHandler.postDelayed(this, 1000);
        }
    };
}
