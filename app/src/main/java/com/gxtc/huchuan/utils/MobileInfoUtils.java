package com.gxtc.huchuan.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Mobile Info Utils
 * create by heliquan at 2017年3月23日
 */
public class MobileInfoUtils {

    /**
     * Get Mobile Type
     *
     * @return
     */
    private static String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * GoTo Open Self Setting Layout
     * Compatible Mainstream Models 兼容市面主流机型
     * adb shell dumpsys activity top
     *
     * @param context
     */
    public static void jumpStartInterface(Context context) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType());
            ComponentName componentName = null;
            // 红米Note4测试通过
            if (getMobileType().equals("Xiaomi")) {
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");

            // 乐视2测试通过
            } else if (getMobileType().equals("Letv")) {
                intent.setAction("com.letv.android.permissionautoboot");

            // 三星Note5测试通过
            } else if (getMobileType().equals("samsung")) {
                componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");

            // 华为测试通过
            } else if (getMobileType().equals("HUAWEI")) {
                componentName = ComponentName.unflattenFromString("com.huawei.systemmanager/.appcontrol.activity.StartupAppControlActivity");
                //componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");

            // VIVO测试通过
            } else if (getMobileType().equals("vivo")) {
                componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");         //com.vivo.permissionmanager/.activity.PurviewTabActivity

            //万恶的魅族
            } else if (getMobileType().equals("Meizu")) {
                componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");

            // OPPO R8205测试通过
            } else if (getMobileType().equals("OPPO")) {
                componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");

            // 360手机 未测试
            } else if (getMobileType().equals("ulong")) {
                componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");

            //努比亚
            } else if(getMobileType().equals("nubia")){
                componentName = ComponentName.unflattenFromString("cn.nubia.security2/cn.nubia.security.appmanage.selfstart.ui.SelfStartActivity");
            } else{
                // 将用户引导到系统设置页面
                if (Build.VERSION.SDK_INT >= 9) {
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                } else if (Build.VERSION.SDK_INT <= 8) {
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
                    intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
                }
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

}
