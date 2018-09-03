package com.gxtc.huchuan;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.gxtc.commlibrary.Constant;
import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.huchuan.helper.MyCrashHandler;
import com.gxtc.huchuan.helper.RongImHelper;
import com.gxtc.huchuan.service.KotlinGrayService;
import com.gxtc.huchuan.utils.JPushUtil;
import com.meituan.android.walle.WalleChannelReader;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;

public class MyApplication extends DefaultApplicationLike {

    public static final String TAG = "Tinker.SampleApplicationLike";
    boolean isBackGround;


    public MyApplication(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true

        mApp = getApplication();
        try {
            getApplication().startService(new Intent( getApplication(), KotlinGrayService.class));  //启动前置进程，提高app的优先级，保证app切换后台不易被系统杀死
        }catch (Exception e){
            e.printStackTrace();
        }

//        installLeakCanary();                      //检测内存溢出仅在debug模式
        initThirdParties();                       //初始化第三方框架
    }

    //调试检测内存溢出
    private void installLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this.getApplication())) {
            return;
        }
//        LeakCanary.install(this.getApplication());
    }

    //初始化第三方服务
    private void initThirdParties() {
        Context context = getApplication().getApplicationContext();

        //自定义全局异常捕获
        MyCrashHandler crashHandler = MyCrashHandler.getInstance();
        crashHandler.init(context);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);

        if (getApplication().getApplicationInfo().packageName.equals(getCurProcessName(getApplication().getApplicationContext()))) {
            LogUtil.i("RongImHelper.init(getApplication());");
            RongImHelper.init(getApplication());
            RongImContext.init(getApplication());
        }

        String packageName = context.getPackageName();                                  // 获取当前包名
        String processName = getProcessName(android.os.Process.myPid());                // 获取当前进程名
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);      // 设置是否为上报进程
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        Bugly.init(context, "7bab334169", Constant.DEBUG, strategy);
        JPushUtil.init(getApplication());

        //友盟部分
        String channel = WalleChannelReader.getChannel(getInstance());
        Log.i("huchuan", "channel  :  " + channel);
        UMShareAPI.init(context, "59a1105c7666130fb3000589");
        UMConfigure.init(context, "59a1105c7666130fb3000589", channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        PlatformConfig.setWeixin("wxe7ecfb0750844217", "00406f427b9ff3ba506a77bb06e3b752"); //微信 wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
        PlatformConfig.setQQZone("1106009777", "15ahWVAPLk9C12Zk");     //QQ

        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.openActivityDurationTrack(false);

        //facebook调试
        Stetho.initialize(Stetho.newInitializerBuilder(getApplication()).enableDumpapp(
                Stetho.defaultDumperPluginsProvider(getApplication())).enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(getApplication())).build());
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }


    private static Application mApp;

    @Nullable
    public static String getCurProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo info : activityManager.getRunningAppProcesses()) {
            if (info.pid == pid) {
                return info.processName;
            }
        }
        return null;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackGround = true;
        }
    }



    public static Application getInstance() {
        return mApp;
    }

}
