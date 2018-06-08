package com.hongfei02chen.xpwechathelper.utils;

import android.content.Context;
import android.util.Pair;

import com.google.gson.Gson;
import com.hongfei02chen.xpwechathelper.MyApplication;
import com.hongfei02chen.xpwechathelper.bean.DaoMaster;
import com.hongfei02chen.xpwechathelper.bean.DaoSession;
import com.hongfei02chen.xpwechathelper.bean.DbChatRoomHelper;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBean;
import com.hongfei02chen.xpwechathelper.bean.JoinMessageBeanDao;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.Query;

import java.util.LinkedList;
import java.util.List;

/**
 * 数据存储
 * created by chenhongfei on 2018/5/14
 */
public class DataUtils {
    public static LinkedList<Pair> mList = new LinkedList<>();
    private static Gson mGson = new Gson();

    public static void enQueue(String group, String nickname) {
        Pair pair = new Pair(nickname, group);
        mList.addLast(pair);
    }

    public static void enQueue(Context context, int msgId, String group, String tempalte, List<String> nicknameList, List<String> usernameList, Long createTime) {
        String nicknameString = mGson.toJson(nicknameList);
        String usernameString = mGson.toJson(usernameList);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, MyApplication.getDbName());
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();

        JoinMessageBeanDao dao = daoSession.getJoinMessageBeanDao();

        final JoinMessageBean bean = new JoinMessageBean(null, msgId, tempalte, nicknameString, usernameString, "", "", group, createTime, 0, 0);
        dao.insert(bean);

        // cp 到公共目录
        FileUtils.copyFile(context.getDatabasePath(MyApplication.getDbName()), FileUtils.getSDCardDir().toString() + "/", MyApplication.getDbName());
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                String content = mGson.toJson(bean) + "\r\n";
//                FileUtils.appendFile(FileUtils.getSaveFilePath(), content);
//            }
//        });

        AppLog.d("=====insertId:" + bean.getId() + " object:" + bean.toString());
    }

    public static Pair deQueue() {
        if (mList.isEmpty()) {
            return null;
        }
        Pair p = mList.getFirst();

        return p;
    }

    public static JoinMessageBean deQueue(DaoSession daoSession) {
        if (daoSession == null) {
            return null;
        }
        JoinMessageBeanDao dao = daoSession.getJoinMessageBeanDao();

        Query<JoinMessageBean> query = dao.queryBuilder().where(JoinMessageBeanDao.Properties.State.eq(0)).limit(1).orderAsc().build();
        List<JoinMessageBean> list = query.list();

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }

        return null;
    }

    public static void insertChatRoom(Context context, String chatRoom, String roomName) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, MyApplication.getDbName());
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();

        DbChatRoomHelper.coverInsert(daoSession, chatRoom, roomName, 1);

    }
}
