package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.gxtc.commlibrary.utils.LogUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.ui.mine.setting.MessageSettingActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;

import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by ALing on 2017/1/13 0013.
 */

public class JPushUtil {
    private static volatile JPushUtil mHelper;

    public static JPushUtil getInstance() {
        if (mHelper == null) {
            synchronized (JPushUtil.class) {
                if (mHelper == null) {
                    mHelper = new JPushUtil();
                }
            }
        }

        return mHelper;
    }

    public static void init(Context context) {
        JPushInterface.init(context);
        boolean sound = SpUtil.getBoolean(context, MessageSettingActivity.Companion.SP_NEW_MESSAGE(), true);
        setSoundAndVibrate(context, sound, !sound);//默认打开声音，关闭震动
        JPushInterface.setDebugMode(true);
    }

    //极光推送的静音处理
    public static void setSoundAndVibrate(Context context, boolean isOpenSound, boolean isOpenVibrate) {
        LogUtil.d("tag", "----isOpenSound--=" + isOpenSound + "----isOpenVibrate--=" + isOpenVibrate);
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(context);
//        builder.statusBarDrawable = R.mipmap.icon;//设置推送的图标
        if (isOpenVibrate && !isOpenSound) {//只有振动
            builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
        } else if (isOpenSound && !isOpenVibrate) {//只有声音
            builder.notificationDefaults = Notification.DEFAULT_SOUND;
        } else if (isOpenSound && isOpenVibrate) {//两个都有
            builder.notificationDefaults = Notification.DEFAULT_ALL;
        } else {//只有呼吸灯
            builder.notificationDefaults = Notification.DEFAULT_LIGHTS;
        }
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    /**
     * 设置要推送的别名
     *
     * @param context
     * @param userCode 使用手机号当别名
     */
    public void setJPushAlias(Activity context, String userCode) {
        if (context == null) return;
        if (JPushInterface.isPushStopped(context)) {
            JPushInterface.resumePush(context);
        }
        JPushInterface.setAliasAndTags(context, userCode, null, new TagAliasCallback() {
            @Override
            public void gotResult(int code, String s, Set<String> set) {
                String logs;
                switch (code) {
                    case 0:
                        logs = "Set tag and alias success";
                        // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                        LogUtil.i("JPushUtil gotResult: " + logs);
                        break;
                    case 6002:
                        LogUtil.i("JPushUtil: " + 6002);
                        break;
                    default:
                        logs = "Failed with errorCode = " + code;
                        LogUtil.i("JPushUtil gotResult: " + logs);
                }
            }
        });
    }

    // 退出登录后关掉极光推送
    public void closeJPush(Activity context) {
        JPushInterface.clearAllNotifications(context);
        JPushUtil.getInstance().setJPushAlias(context, null);
        JPushUtil.getInstance().setJPushAlias(context, "");
        JPushInterface.stopPush(context);
    }
}
