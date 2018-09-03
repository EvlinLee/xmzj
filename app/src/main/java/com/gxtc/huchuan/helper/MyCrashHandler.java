package com.gxtc.huchuan.helper;

import android.content.Context;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/10/8.
 */

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {


    private static MyCrashHandler myCrashHandler ;
    private        Context        context;

    //1.私有化构造方法
    private MyCrashHandler(){

    }

    public static synchronized MyCrashHandler getInstance(){
        if(myCrashHandler!=null){
            return myCrashHandler;
        }else {
            myCrashHandler  = new MyCrashHandler();
            return myCrashHandler;
        }
    }

    public void init(Context context){
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
