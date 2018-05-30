package com.hongfei02chen.wxautoatreply;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hongfei02chen.wxautoatreply.utils.CollectionUtils;
import com.hongfei02chen.wxautoatreply.utils.ViewUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 进入聊天页面，自动发消息
 * <p>
 * created by chenhongfei on 2018/5/7
 */
public class TimerSendService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReply";

    private static final long mDelayTime = 500;


    private static String mForegroundPackageName;

    private String mGroup;
    private String mNickname;

    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();

    private LinkedList<String> mSendList = new LinkedList<String>() {{
        add("木头人,@kong ,1526288641");
        add("机器人,@kong ,1526288641");
        add("罗伯特,@kong ,1526288641");
    }};

    private boolean mInSendView = false;

    public static final String SEND_CONTENT_1 = "君不见，黄河之水天上来，奔流到海不复回。"+
            "https://shimo.im/docs/Yghjb1Y2hZ8ee0o5/\n";
    public static final String SEND_CONTENT_2 = "君不见，高堂明镜悲白发，朝如青丝暮成雪。"+
            "https://shimo.im/docs/Yghjb1Y2hZ8ee0o5/\n";

    @Override
    public void onCreate() {
        super.onCreate();
        mSendHandler.postDelayed(mRunnable, mDelayTime);
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.d(TAG, "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                mForegroundPackageName = event.getPackageName().toString();
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
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
    public void findGroupAndClick(String viewId, String groupName, String nickname) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
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

                Log.i(TAG, String.format("=================findGroupAndClick title:%s, content:%s", title, content));
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    continue;
                }

                // 不包含这个群组
                if (!title.equals(groupName)) {
                    continue;
                }


                // 模拟点击，进入聊天窗口
                mInSendView = true;
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d(TAG, String.format("====match group:%s, nickname:%s and click", title, nickname));
                break;
            }
        }
    }


    private void fillAndSend(AccessibilityNodeInfo rootNode, String group, String nickname) {
        if (!mInSendView) {
            return;
        }
        if (rootNode == null) {
            if (mInSendView) {
                mInSendView = false;
                Log.d(TAG, "===============nullnullnullnull======pressBackButton");
                ViewUtils.clickView(rootNode, ViewUtils.RESOURCE_ID_BACK, "android.widget.ImageView");
            }
            return;
        }
        Log.d(TAG, String.format(" ===============fillAndSend group:%s, nickname:%s ", group, nickname));
        boolean flag = findEditText(rootNode, i + ":" + SEND_CONTENT_1);
        Log.d(TAG, " ===============flag:" + flag);
        if (flag) {
            send();
        }
        flag = findEditText(rootNode, i + ":" + SEND_CONTENT_2);
        if (flag) {
            send();
        }

        mInSendView = false;
        Log.d(TAG, "=====================pressBackButton");
        ViewUtils.clickView(rootNode, ViewUtils.RESOURCE_ID_BACK, "android.widget.ImageView");
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
    }

    public boolean isAppForeground(String packetName) {
        return packetName.equals(mForegroundPackageName);
    }

    int i = 0;
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (!mSendList.isEmpty() && !mInSendView && isAppForeground(ViewUtils.MM_PNAME)) {
                    String content = mSendList.get(i% mSendList.size());

                    Log.d(TAG, "get content:" + content);
                    if (!TextUtils.isEmpty(content)) {
                        String[] splitArray = content.split(",");
                        if (null != splitArray && splitArray.length == 3) {
                            mGroup = splitArray[0];
                            mNickname = splitArray[1];

                            findGroupAndClick(ViewUtils.RESOURCE_ID_ITEM, mGroup, mNickname);
                            i++;
                        }
                    }
                } else {
//                    Log.d(TAG, "empty running=================" + mSendList.size() + " mInSendView:" + mInSendView + "  isAppForeground:"
//                            + isAppForeground( ViewUtils.MM_PNAME) + " getRootInActiveWindow:" + getRootInActiveWindow());
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

            if (i % 3 == 0) {
                mSendHandler.postDelayed(this, mDelayTime * 8);
            } else {
                mSendHandler.postDelayed(this, mDelayTime);
            }
        }
    };
}
