package com.gxtc.huchuan.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gxtc.huchuan.bean.DaoMaster;
import com.gxtc.huchuan.bean.dao.UserDao;

import org.greenrobot.greendao.database.Database;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/6/23.
 * 数据库帮助类,用于更新数据库版本
 * @link MigrationHelper
 */

public class MyOpenHelper extends DaoMaster.OpenHelper{

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新 有几个表升级都可以传入到下面
        MigrationHelper.getInstance().migrate(db,UserDao.class);
    }
}
