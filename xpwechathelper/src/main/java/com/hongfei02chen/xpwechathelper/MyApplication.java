package com.hongfei02chen.xpwechathelper;

import android.app.Application;

import com.hongfei02chen.xpwechathelper.bean.DaoMaster;
import com.hongfei02chen.xpwechathelper.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * created by chenhongfei on 2018/6/6
 */
public class MyApplication extends Application {
    public static final boolean ENCRYPTED = false;
    private DaoSession daoSession;
    public static MyApplication mInstance;
    public static String DB_NAME = "wechat-magic.db";
    public static String DB_NAME_1 = "wechat-magic1.db";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initDaoSession();
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static String getDbName1() {
        return DB_NAME_1;
    }
    private void initDaoSession() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "wechat-magic-encrypted.db" :DB_NAME );
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
