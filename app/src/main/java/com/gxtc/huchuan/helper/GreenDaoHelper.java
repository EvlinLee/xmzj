package com.gxtc.huchuan.helper;


import android.database.sqlite.SQLiteDatabase;

import com.gxtc.huchuan.MyApplication;
import com.gxtc.huchuan.bean.DaoMaster;
import com.gxtc.huchuan.bean.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * 数据库帮助类
 * Created by Steven on 16/11/4.
 */

public class GreenDaoHelper {

    public static final String DB_NAME = "csq";

    private static GreenDaoHelper mHelper;
    private MyOpenHelper mOpenHelper;

    private GreenDaoHelper() {
        //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MyApplication.getInstance().getApplicationContext(),DB_NAME);
        mOpenHelper = new MyOpenHelper(MyApplication.getInstance(), DB_NAME, null);
    }

    public static GreenDaoHelper getInstance() {
        if (mHelper == null) {
            synchronized (GreenDaoHelper.class) {
                if (mHelper == null) {
                    mHelper = new GreenDaoHelper();
                }
            }
        }
        return mHelper;
    }

    public DaoSession getSeeion() {
        return new DaoMaster(getWritableDatabase()).newSession();
    }

    private SQLiteDatabase getWritableDatabase() {
        return mOpenHelper.getWritableDatabase();
    }

}
