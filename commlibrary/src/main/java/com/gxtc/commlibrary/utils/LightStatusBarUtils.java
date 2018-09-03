package com.gxtc.commlibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.gxtc.commlibrary.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Loyea.com on 7月20日.
 */
public class LightStatusBarUtils {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setLightStatusBar(Activity activity, boolean dark) {
        switch (RomUtils.getLightStatausBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
                setMIUILightStatusBar(activity, dark);
                break;

            case RomUtils.AvailableRomType.FLYME:
                setFlymeLightStatusBar(activity, dark);
                break;

            case RomUtils.AvailableRomType.ANDROID_NATIVE://6.0以上
                setAndroidNativeLightStatusBar(activity, dark);
                break;

            case RomUtils.AvailableRomType.NA://6.0以下
                setAndroidStatusBarBelowLOL(activity, dark);
                break;
        }
    }

    private static boolean setMIUILightStatusBar(Activity activity, boolean darkmode) {
        if (darkmode) {
            /*Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);*/

            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);

                //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setAndroidNativeLightStatusBar(activity, darkmode);
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void setFlymeLightStatusBar(Activity activity, boolean dark) {
        /*boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;*/

        StatusbarColorUtils.setStatusBarDarkIcon(activity, dark);
    }

    /**
     * 6.0以上
     *
     * @param activity
     * @param dark
     */
    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        //android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                int visibi = decor.getSystemUiVisibility();
                visibi |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decor.setSystemUiVisibility(visibi);

            } else {
                // We want to change tint color to white again.
                // You can also record the flags in advance so that you can turn UI back completely if
                // you have set other flags before, such as translucent or full screen.
                int visibi = decor.getSystemUiVisibility();
                visibi &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                decor.setSystemUiVisibility(visibi);
            }
        }
    }

    private static void setAndroidStatusBarBelowLOL(Activity activity, boolean dark) {
        //5.0以上6.0以下
        int buildVersion = Build.VERSION.SDK_INT;
        if (buildVersion >= Build.VERSION_CODES.LOLLIPOP&&buildVersion<Build.VERSION_CODES.M) {
            if (dark){
                Window window = activity.getWindow();
                //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色
                window.setStatusBarColor(Color.parseColor("#ffd2d2d2"));
            }
        }
    }

}
