package com.hongfei02chen.wxautoatreply;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hongfei02chen.wxautoatreply.utils.CollectionUtils;
import com.hongfei02chen.wxautoatreply.utils.FileUtils;
import com.hongfei02chen.wxautoatreply.utils.ViewUtils;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * 监听微信列表的消息，从中获取指定群的新人nicknam
 * <p>
 * created by chenhongfei on 2018/5/7
 */
public class GetFromListService extends AccessibilityService {
    private static final String TAG = "AutoAtAndReplyService";

    private Handler handler = new Handler();
    private Handler mSaveHandler = new Handler();


    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.d(TAG, "get event = " + eventType);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findListChangeContent(getRootInActiveWindow(), ViewUtils.RESOURCE_ID_ITEM);
                    }
                });

                break;
        }
    }

    @Override
    public void onInterrupt() {

    }

    //resource-id : com.tencent.mm:id/jz
    public void findListChangeContent(AccessibilityNodeInfo rootNode, String viewId) {
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

//                Log.i(TAG, String.format("=================title:%s, content:%s", title, content));
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                    continue;
                }

                // 不包含这个群组
                if (!ViewUtils.mGroupList.contains(title.toLowerCase())) {
                    continue;
                }

                String nickname = ViewUtils.regexMatcherNickname(content);
                if (TextUtils.isEmpty(nickname)) {
                    continue;
                }

                // 判断at 加入之后，是否曾经at过
//                Rect screenRect = new Rect();
//                nodeInfo.getBoundsInScreen(screenRect);
////                Log.i(TAG, "================== content: " + content +  " screenRect.bottom:" + screenRect.bottom);
//                if (isEverAt(rootNode, screenRect.bottom)) {
//                    continue;
//                }

                // 保存nickname
//                SharedPreferRecord.getInstance(this).edit().putLong(nickname, System.currentTimeMillis()).commit();
                Log.d(TAG, String.format("====group:%s, nickname:%s", title, nickname));
                saveFile(title, nickname);
            }
        }

    }

    private void saveFile(final String group, final String nickname) {
        mSaveHandler.post(new Runnable() {
            @Override
            public void run() {
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        String content = group + "," + nickname + "," + System.currentTimeMillis() / 1000 + "\r\n";
                        FileUtils.appendFile(FileUtils.getSaveFilePath(), content);
                    }
                });
            }
        });
    }
}
