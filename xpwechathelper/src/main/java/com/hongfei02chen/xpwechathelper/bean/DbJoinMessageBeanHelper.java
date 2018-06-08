package com.hongfei02chen.xpwechathelper.bean;

import com.hongfei02chen.xpwechathelper.MyApplication;

import java.util.List;

/**
 * created by chenhongfei on 2018/6/6
 */
public class DbJoinMessageBeanHelper {


    public static List<JoinMessageBean> queryList() {
        DaoSession daoSession = MyApplication.getInstance().getDaoSession();
        JoinMessageBeanDao dao = daoSession.getJoinMessageBeanDao();

        List<JoinMessageBean> list = dao.queryBuilder().orderDesc().list();
        return list;
    }
}
