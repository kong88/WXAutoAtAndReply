package com.hongfei02chen.xpwechathelper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.ui.base.BaseMultiFragmentActivity;
import com.hongfei02chen.xpwechathelper.bean.ChatRoomBean;
import com.hongfei02chen.xpwechathelper.bean.ChatRoomBeanDao;
import com.hongfei02chen.xpwechathelper.bean.DaoMaster;
import com.hongfei02chen.xpwechathelper.bean.DaoSession;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBeanDao;
import com.hongfei02chen.xpwechathelper.eventbus.MessageEvent;
import com.hongfei02chen.xpwechathelper.ui.fragment.GroupFragment;
import com.hongfei02chen.xpwechathelper.ui.fragment.MessageListFragment;
import com.hongfei02chen.xpwechathelper.ui.fragment.StatusFragment;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;
import com.hongfei02chen.xpwechathelper.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseMultiFragmentActivity {
    private Handler mTimerHandler = new Handler();
    private static final long mDelayTime = 5000;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void loadData() {
        mTimerHandler.postDelayed(mRunnable, mDelayTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showModuleActiveInfo(false);
//        transferDbData();
        startService(new Intent(this, TransferDataService.class));
    }

    /**
     * 模块激活信息
     *
     * @param isModuleActive
     */
    private void showModuleActiveInfo(boolean isModuleActive) {
        EventBus.getDefault().post(new MessageEvent("xpose hook", isModuleActive));
    }

    private void transferDbData() {
        FileUtils.copyFile(new File(FileUtils.getSDCardDir().toString(), MyApplication.getDbName()), getDatabasePath("..").getParent() + "/", MyApplication.getDbName1());
        queryList();
    }

    private void queryList() {
        Database db1 = new DaoMaster.DevOpenHelper(this, MyApplication.getDbName1()).getWritableDb();
        DaoSession daoSession1 = new DaoMaster(db1).newSession();
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();


        JoinMessageBeanDao messageBeanDao1 = daoSession1.getJoinMessageBeanDao();
        JoinMessageBeanDao messageBeanDao = daoSession.getJoinMessageBeanDao();
        ChatRoomBeanDao roomBeanDao1 = daoSession1.getChatRoomBeanDao();
        ChatRoomBeanDao roomBeanDao = daoSession.getChatRoomBeanDao();

        List<JoinMessageBean> list1 = messageBeanDao1.queryBuilder().orderAsc().build().list();

        List<JoinMessageBean> existList = messageBeanDao.queryBuilder().list();
        Map<Integer, JoinMessageBean> messageBeanMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existList)) {
            for (JoinMessageBean bean : existList) {
                messageBeanMap.put(bean.getMsgId(), bean);
            }
        }

        AppLog.debug(" ====message list size:" + list1.size());
        if (!CollectionUtils.isEmpty(list1)) {
            for (int i = 0; i < list1.size(); i++) {

                JoinMessageBean srcMessageBean = list1.get(i);
                if (messageBeanMap.get(srcMessageBean.getMsgId()) != null) {
                    continue;
                }
                AppLog.debug("insert message:" + list1.get(i).toString());
                daoSession.insert(srcMessageBean);
            }
        }


        List<ChatRoomBean> roomBeanList1 = roomBeanDao1.queryBuilder().list();
        List<ChatRoomBean> existRoomBeanList = roomBeanDao.queryBuilder().list();
        Map<String, ChatRoomBean> roomBeanMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(existRoomBeanList)) {
            for (ChatRoomBean bean : existRoomBeanList) {
                roomBeanMap.put(bean.getChatRoom(), bean);
            }
        }

        List<ChatRoomBean> updateList = new ArrayList<>();
        AppLog.debug("room list size:" + roomBeanList1.size());
        if (!CollectionUtils.isEmpty(roomBeanList1)) {

            for (ChatRoomBean bean : roomBeanList1) {
                String chatRoom = bean.getChatRoom();
                String roomName = bean.getRoomName();
                if (TextUtils.isEmpty(chatRoom) || TextUtils.isEmpty(roomName)) {
                    continue;
                }
                if (null != roomBeanMap.get(chatRoom) && roomName.equals(roomBeanMap.get(chatRoom).getRoomName())) {
                    continue;
                }
                updateList.add(bean);
            }
        }
        for (ChatRoomBean bean : updateList) {
            DbChatRoomHelper.coverInsert(daoSession, bean.getChatRoom(), bean.getRoomName(), bean.getState());
        }
    }

    @Override
    protected FragmentTabHost getFragmentTabHost() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);// 找到TabHost选项卡
        return mTabHost;
    }

    @Override
    protected Class[] getFragmentArray() {
        return new Class[]{StatusFragment.class, GroupFragment.class, MessageListFragment.class};
    }

    @Override
    protected String[] getTextArray() {
        return new String[]{"状态", "群组", "消息"};
    }

    @Override
    protected int[] getIconArray() {
        return new int[]{R.color.transparent, R.color.transparent, R.color.transparent};
    }

    @Override
    protected Bundle getBundle(int index) {
        return null;
    }

    @Override
    protected int getContentId() {
        return R.id.realcontent;
    }

    @Override
    protected View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_bottom_tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
        imageView.setImageResource(mIconArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.tv_name);
        textView.setText(mTextArray[index]);

        return view;
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            transferDbData();
            mTimerHandler.postDelayed(mRunnable, mDelayTime);
        }
    };
}
