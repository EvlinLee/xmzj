package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.SpUtil;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.bean.event.EventRefreshConversationBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.message.MessageFragment;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;
import com.gxtc.huchuan.ui.mine.setting.SettingActivity;

import io.rong.imkit.RongIM;

/**
 * Created by zzg on 2018/1/31.
 */

public class ReLoginUtil {

    //重新登录（因为token过期）需要把之前的数据清除，重新获取
    public static void ReloginTodo(Activity activity) {
        SpUtil.remove(activity, MessageFragment.KEY_NUM());
        SpUtil.remove(activity, MessageFragment.KEY_NUM());
        SpUtil.remove(activity, SettingActivity.SP_SOUND());
        UserManager.getInstance().deleteUser();
        EventBusUtil.post(new EventLoginBean(EventLoginBean.EXIT));
        RongIM.getInstance().logout();
        //退出登录收不到推送
        JPushUtil.getInstance().closeJPush(activity);
        Intent intent = new Intent(activity, LoginAndRegisteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}
