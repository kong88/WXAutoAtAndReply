package com.hongfei02chen.xpwechathelper;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hongfei02chen.xpwechathelper.bean.DaoSession;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;
import com.hongfei02chen.xpwechathelper.utils.DataUtils;
import com.hongfei02chen.xpwechathelper.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 进入聊天页面，自动发消息
 * <p>
 * created by chenhongfei on 2018/5/7
 */
public class SendService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReply";

    private static final long mDelayTime = 5000;


    private static String mForegroundPackageName;

    private String mGroup;
    private String mNickname;
    private List<String> mNicknameList = new ArrayList<>();
    private JoinMessageBean mMessageBean;

    private Handler mHandler = new Handler();
    private Handler mSendHandler = new Handler();
    private Handler mViewHandler = new Handler();


    private boolean mInSendView = false;
    private DaoSession mDaoSession;
    private SendState.S state = SendState.S.IDLE;

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
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (!ViewUtils.MM_PNAME.equals(event.getPackageName())) {
                    return;
                }
                if (!CollectionUtils.isEmpty(event.getText())) {
                    return;
                }
//                AppLog.debug(TAG, " source:" + event.getSource() + " \nclassName:" + event.getClassName() + " \ntext:" + event.getText() + " \naction:" + event.getAction() + " \ngetPackageName" + event.getPackageName());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                        fillAndSend(rootNode, mGroup, mNickname, mNicknameList);
                    }
                });
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:// paste at , text changed
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:// 弹出 at list window，at list window 消失
                if ("com.meizu.flyme.input".equals(event.getPackageName().toString())) {
                    break;
                }
                mForegroundPackageName = event.getPackageName().toString();
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
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

                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                mInSendView = true;
                Log.d(TAG, String.format("====match group:%s, nickname:%s and click", title, nickname));
                break;
            }
        }
    }


    private void fillAndSend(final AccessibilityNodeInfo rootNode, String group, String nickname, final List<String> nicknameList) {
        if (!mInSendView || (state != SendState.S.IDLE && state != SendState.S.END)) {
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
        Log.d(TAG, String.format(" ===============begin fillAndSend() group:%s, nickname:%s ", group, nickname));
        state = SendState.S.PASTE_AT;
        mViewHandler.post(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case IDLE:
                        break;
                    case PASTE_AT:
                        if (CollectionUtils.isEmpty(nicknameList)) {
                            state = SendState.S.CLICK_SEND;
                            // 继续at nickname or enter if 流程
                            mViewHandler.postDelayed(this, 500);
                        } else {
                            boolean flag = findEditTextAt(rootNode);
                            AppLog.debug(TAG, String.format("=====exec 00000 state:%s, flag:%b", state, flag));

                            state = SendState.S.SELECT_AT_NICKNAME;
                            // 继续at nickname or enter if 流程
                            mViewHandler.postDelayed(this, 1500);
                        }
                        break;
                    case SELECT_AT_NICKNAME:
                        final String nickname = mNicknameList.get(0);
                        // 1、执行at nickname；如果找不到，可能是view 没有弹出来，也可能是nickname 离开了群组
                        boolean found = findListViewAndClickItem(nickname);
                        AppLog.debug(TAG, String.format("=====exec 11111 state:%s, at nickname:%s, flag:%b ", state, nickname, found));

                        if (found) {
                            mNicknameList.remove(0);
                            state = SendState.S.PASTE_AT;
                            mViewHandler.postDelayed(this, 500);
                        } else {
                            // 如果找不到，则关闭窗口，然后粘贴nickname，然后往下执行


                            state = SendState.S.PASTE_NICKNAME;
                            mViewHandler.postDelayed(this, 500);
                        }

                        break;
                    case PASTE_NICKNAME:
                        ViewUtils.clickView(getRootInActiveWindow(), "com.tencent.mm:id/ht", "android.widget.ImageView");
                        ViewUtils.clickView(getRootInActiveWindow(), "com.tencent.mm:id/hs", "android.widget.LinearLayout");
                        String nickname2 = mNicknameList.remove(0);
                        boolean flagNickname = findEditTextContent(getRootInActiveWindow(), nickname2 + " ");
                        AppLog.debug(TAG, String.format("=====exec 1.51.51.51.51.5 state:%s, at nickname:%s, flag:%b ", state, nickname2, flagNickname));
                        state = SendState.S.PASTE_AT;
                        mViewHandler.postDelayed(this, 500);
                        break;
                    case CLICK_SEND:
                        boolean flagSend = send();
                        AppLog.debug(TAG, String.format("=====exec 22222 send nickname state:%s, flag:%b", state, flagSend));
                        state = SendState.S.PASTE_CONTENT;
                        mViewHandler.postDelayed(this, 500);
                        break;
                    case PASTE_CONTENT:
                        boolean flag = findEditTextContent(rootNode, ViewUtils.SEND_CONTENT);
                        AppLog.debug(TAG, String.format("=====exec 33333 fill content state:%s, flag:%b", state, flag));
                        if (flag) {
                            state = SendState.S.CLICK_SEND_2;
                        } else {
                            state = SendState.S.END;
                        }
                        mViewHandler.postDelayed(this, 500);
                        break;
                    case CLICK_SEND_2:
                        boolean flagSend2 = send();
                        AppLog.debug(TAG, String.format("=====exec 44444 send content state:%s, flag:%b", state, flagSend2));
                        // update database
                        if (null != mMessageBean) {
                            mMessageBean.setState(1);
                            mDaoSession.getJoinMessageBeanDao().update(mMessageBean);
                            mMessageBean = null;
                            AppLog.debug(TAG, "=====exec 44444 update database =====");
                        }
                        state = SendState.S.END;
                        mViewHandler.postDelayed(this, 500);
                        break;
                    case END:
                        mInSendView = false;
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        AppLog.debug(TAG, "=====exec 55555 press back end!!!");
                        break;
                }
            }
        });
    }

    public void pressGlobalActionBack() {
        mViewHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            }
        }, 1000);
    }


    private boolean findListViewAndClickItem(String nickname) {
        boolean found = false;
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeList = rootNode.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_AT_LISTVIEW);
//        AppLog.debug(TAG, " nodeList:" + nodeList.size() + " rootNode;" + rootNode);
        if (!CollectionUtils.isEmpty(nodeList)) {
            int position = -1;
            for (int i = 0; i < nodeList.size(); i++) {
                AccessibilityNodeInfo nodeInfo = nodeList.get(i);
                AppLog.debug(TAG, " nodeList i:" + i + " className:" + nodeInfo.getClassName());
                if (!"android.widget.ListView".equals(nodeInfo.getClassName())) {
                    continue;
                }

                int forNum = 100;
                while (forNum > 0) {
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    AppLog.debug(TAG, " list current for:" + forNum + " getChildCount: " + nodeInfo.getChildCount());
                    forNum--;
                    for (int j = 0; j < nodeInfo.getChildCount(); j++) {
                        AccessibilityNodeInfo childNode = nodeInfo.getChild(j);

                        List<AccessibilityNodeInfo> textNodeList = childNode.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_AT_NICKNAME);
                        if (CollectionUtils.isEmpty(textNodeList)) {
                            continue;
                        }
                        for (AccessibilityNodeInfo textNode : textNodeList) {
                            AppLog.debug(TAG, String.format(" f==========================childPosition:%d,  =ViewUtils.getNodeText(textNode):%s, size:%d , nickname:%s ",
                                    j, ViewUtils.getNodeText(textNode), textNodeList.size(), nickname));
                            if ("android.widget.TextView".equals(textNode.getClassName()) && nickname.equals(ViewUtils.getNodeText(textNode))) {
                                AppLog.debug(TAG, " find nickname success=====================================:");
                                childNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        return found;
    }

    private boolean findEditTextAt(AccessibilityNodeInfo rootNode) {
        return findEditText(rootNode, "label", "@");
    }

    private boolean findEditTextContent(AccessibilityNodeInfo rootNode, String content) {
        return findEditText(rootNode, "content", content);
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode, String label, String content) {
        int count = rootNode.getChildCount();

//        Log.d(TAG, "=========== findEditText root class=" + rootNode.getClassName() + "," + rootNode.getText() + "," + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo == null) {
                Log.d(TAG, "nodeinfo = null");
                continue;
            }

            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                AppLog.debug(TAG, "================== findEditText lable: " + label + " content:" + content);
//                Bundle arguments = new Bundle();
//                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
//                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLEAR_SELECTION);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS);
                nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                nodeInfo.performAction(AccessibilityNodeInfo.FOCUS_INPUT);

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(label, content);
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }
//
            if (findEditText(nodeInfo, label, content)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 寻找窗体中的“发送”按钮，并且点击。
     */
    private boolean send() {
        return send("");
    }

    private boolean send(String nickname) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return false;
        }
        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(ViewUtils.RESOURCE_ID_BUTTON);
        if (list == null) {
            return false;
        }
        for (AccessibilityNodeInfo node : list) {
            if ("android.widget.Button".equals(node.getClassName()) && node.isEnabled()) {
                Log.i(TAG, "================== click send ");
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return true;
            }
        }

        return false;
    }

    public boolean isAppForeground(String packetName) {
        return packetName.equals(mForegroundPackageName);
    }

    @Override
    public void onDestroy() {
        AppLog.debug(TAG, " process onDestroy======");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        AppLog.debug(TAG, " process onLowMemory======");
        super.onLowMemory();
    }


    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                AppLog.debug(TAG, " post Delayed=== mInSendView：" + mInSendView + " isAppForeground(ViewUtils.MM_PNAME):" + isAppForeground(ViewUtils.MM_PNAME) + " packageName:" + mForegroundPackageName);
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
                                mNicknameList.clear();
                                mNicknameList.addAll(nicknameList);
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


    Runnable mStateRunnable = new Runnable() {
        @Override
        public void run() {
            switch (state) {
                case IDLE:
                    break;
                case PASTE_AT:
                    break;
                case SELECT_AT_NICKNAME:
                    break;
                case CLICK_SEND:
                    break;
                case PASTE_CONTENT:
                    break;
                case CLICK_SEND_2:
                    break;
            }

        }
    };
}
