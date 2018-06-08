package com.hongfei02chen.xpwechathelper.bean;

import android.text.TextUtils;

import com.hongfei02chen.xpwechathelper.MyApplication;
import com.hongfei02chen.xpwechathelper.utils.AppLog;
import com.hongfei02chen.xpwechathelper.utils.CollectionUtils;

import org.greenrobot.greendao.query.Query;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/6
 */
public class DbChatRoomHelper {
    public static String getRoomName(String chatRoom) {
        if (TextUtils.isEmpty(chatRoom)) {
            return null;
        }
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();
        ChatRoomBeanDao dao = daoSession.getChatRoomBeanDao();
        Query<ChatRoomBean> query = dao.queryBuilder().where(ChatRoomBeanDao.Properties.ChatRoom.eq(chatRoom)).build();
        List<ChatRoomBean> list = query.list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0).getRoomName();
    }

    public static Long insert(DaoSession daoSession, String chatRoom, String roomName, int state) {
        /**
         * 记录不存在才插入
         */
        ChatRoomBeanDao dao = daoSession.getChatRoomBeanDao();
        Query<ChatRoomBean> query = dao.queryBuilder().where(ChatRoomBeanDao.Properties.ChatRoom.eq(chatRoom)).build();
        if (query != null) {
            List<ChatRoomBean> list = query.list();
            if (!CollectionUtils.isEmpty(list)) {
                return null;
            }
        }

        ChatRoomBean chatRoomBean = new ChatRoomBean(null, chatRoom, roomName, state);
        return daoSession.insert(chatRoomBean);
    }

    public static Long coverInsert(DaoSession daoSession, String chatRoom, String roomName, int state) {
        /**
         * 记录存在则删除
         */
        ChatRoomBeanDao dao = daoSession.getChatRoomBeanDao();
        List<ChatRoomBean> list = dao.queryBuilder().where(ChatRoomBeanDao.Properties.ChatRoom.eq(chatRoom)).list();

        if (!CollectionUtils.isEmpty(list)) {
            for (ChatRoomBean bean : list) {
                dao.delete(bean);
            }
        }

        ChatRoomBean chatRoomBean = new ChatRoomBean(null, chatRoom, roomName, state);
        Long insertId = daoSession.insert(chatRoomBean);
        AppLog.debug("=====coverInsert insertId:" + insertId + " object:" + roomName);
        return insertId;
    }

    public static List<ChatRoomBean> queryList() {
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();
        ChatRoomBeanDao dao = daoSession.getChatRoomBeanDao();
        Query<ChatRoomBean> query = dao.queryBuilder().build();
        if (query == null) {
            return null;
        }
        List<ChatRoomBean> list = query.list();
        return list;
    }
}
