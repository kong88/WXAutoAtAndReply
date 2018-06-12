package com.hongfei02chen.xpwechathelper.bean;

import com.hongfei02chen.xpwechathelper.MyApplication;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/6
 */
public class DbJoinMessageBeanHelper {


    public static List<JoinMessageBean> queryList() {
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();
        JoinMessageBeanDao dao = daoSession.getJoinMessageBeanDao();

        List<JoinMessageBean> list = dao.queryBuilder().orderDesc(JoinMessageBeanDao.Properties.MsgId).list();
        return list;
    }

    public static List<JoinMessageBean> queryList(int state) {
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();
        JoinMessageBeanDao dao = daoSession.getJoinMessageBeanDao();

       QueryBuilder<JoinMessageBean> queryBuilder = dao.queryBuilder().orderDesc(JoinMessageBeanDao.Properties.MsgId);
       if (state >= 0) {
           queryBuilder = queryBuilder.where(JoinMessageBeanDao.Properties.State.eq(state));
       }
        List<JoinMessageBean> list = queryBuilder.list();
        return list;
    }
}
