package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

/**
 * Created by 宋家任 on 2017/6/7.
 */

public class GoToActivityIfLoginUtil {

    public static void goToActivity(Fragment fragment, Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = new Intent(fragment.getContext(), toClass);
            fragment.startActivity(intent);
        } else {
            GotoUtil.goToActivity(fragment, LoginAndRegisteActivity.class);
        }

    }

    public static void goToActivity(Activity activity, Class<?> toClass) {
        if (UserManager.getInstance().isLogin()) {
            Intent intent = new Intent(activity, toClass);
            activity.startActivity(intent);
        } else {
            GotoUtil.goToActivity(activity, LoginAndRegisteActivity.class);
        }
    }
}
