package com.hongfei02chen.xpwechathelper;

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

import com.hongfei02chen.xpwechathelper.bean.DaoSession;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;
import com.hongfei02chen.xpwechathelper.utils.DataUtils;
import com.hongfei02chen.xpwechathelper.utils.ViewUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 进入聊天页面，自动发消息
 * <p>
 * created by chenhongfei on 2018/5/7
 */
public class SendService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReply";

    private static final long mDelayTime = 3000;


    private static String mForegroundPackageName;

    private String mGroup;
    private String mNickname;
    private JoinMessageBean mMessageBean;

    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();

    private LinkedList<String> mSendList = new LinkedList<String>() {{
//        add("123test,@kong ,1526288641");
        add("木头人,@kong ,1526288641");
//        add("123test,@Jim ,1526288641");
        add("木头人,@Jim ,1526288641");
    }};

    private boolean mInSendView = false;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mSendHandler.postDelayed(mRunnable, mDelayTime);
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, MyApplication.getDbName1());
//        Database db = helper.getWritableDb();
//        mDaoSession = new DaoMaster(db).newSession();
        mDaoSession = MyApplication.getInstance().getDaoSession();
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
        AppLog.verbose(TAG, " view list:" + list.size());
        for (AccessibilityNodeInfo nodeInfo : list) {
            if (ViewUtils.RESOURCE_CLASS_LL.equals(nodeInfo.getClassName())) {

                // 从nodeInfo 查找子view
                List<AccessibilityNodeInfo> titleList = nodeInfo.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_TITLE);
//                List<AccessibilityNodeInfo> contentList = nodeInfo.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_CONTENT);
                AppLog.verbose(TAG, " title list:" + titleList.size());
                if (CollectionUtils.isEmpty(titleList)) {
                    continue;
                }

                // 每个nodeInfo节点只有一个title和content元素
                String title = ViewUtils.getNodeText(titleList.get(0));

                Log.i(TAG, String.format("=================findGroupAndClick title:%s", title));
                if (TextUtils.isEmpty(title)) {
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
        boolean flag = findEditText(rootNode, nickname);
        Log.d(TAG, " ===============flag:" + flag);
        if (flag) {
            send();
        }
        flag = findEditText(rootNode, ViewUtils.SEND_CONTENT);
        if (flag) {
            send();
        }

        // update database
        if (null != mMessageBean) {
            mMessageBean.setState(1);
            mDaoSession.getJoinMessageBeanDao().update(mMessageBean);
            mMessageBean = null;
        }
        mInSendView = false;
        Log.d(TAG, "=====================pressBackButton");
        pressBackButton();
    }

    public static void pressBackButton() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    public boolean isAppForeground(String packetName) {
        return packetName.equals(mForegroundPackageName);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                // 微信在前台运行
                if (!mInSendView && isAppForeground(ViewUtils.MM_PNAME)) {
                    JoinMessageBean messageBean = DataUtils.deQueue(mDaoSession);
                    if (messageBean != null) {
                        AppLog.debug(TAG, "xposed :  messageBean:" + messageBean.toString());
                        String chatRoom = messageBean.getChatRoom();
                        List<String> nicknameList = messageBean.getNicknameList();
                        if (!TextUtils.isEmpty(chatRoom) && !CollectionUtils.isEmpty(nicknameList)) {
                            String roomName = DbChatRoomHelper.getRoomName(chatRoom);
                            String formatNickname = ViewUtils.formatAtList(nicknameList);
                            AppLog.debug(TAG, "xposed : " + roomName + " at:" + formatNickname);
                            if (!TextUtils.isEmpty(roomName) && !TextUtils.isEmpty(formatNickname)) {
                                mGroup = roomName;
                                mNickname = formatNickname;
                                mMessageBean = messageBean;
                                findGroupAndClick(ViewUtils.RESOURCE_ID_ITEM, roomName, formatNickname);
                            }
                        }
                    }
                }


            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

            mSendHandler.postDelayed(this, mDelayTime);
        }
    };
}
