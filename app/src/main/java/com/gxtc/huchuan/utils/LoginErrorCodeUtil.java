package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.gxtc.commlibrary.utils.EventBusUtil;
import com.gxtc.commlibrary.utils.GotoUtil;
import com.gxtc.commlibrary.utils.ToastUtil;
import com.gxtc.huchuan.Constant;
import com.gxtc.huchuan.bean.event.EventLoginBean;
import com.gxtc.huchuan.data.UserManager;
import com.gxtc.huchuan.ui.mine.loginandregister.LoginAndRegisteActivity;

import static com.gxtc.huchuan.bean.event.EventLoginBean.TOKEN_OVERDUCE;

/**
 * Created by sjr on 2017/3/9.
 * token过期或者用户不存在时弹出对话框处理错误码
 */

public class LoginErrorCodeUtil {
    public static final int NOT_NETWORK_400 = 400;
    public static final int RESOLVE_ERROR = 401;
    public static final int SERVER_ERROR = 500;

    public static final int TOKEN_OVERDUE_10001 = 10001;         // token 过期
    public static final int TOKEN_NULL_10002 = 10002;         // 没有token
    public static final int SERVER_ERROR_99999 = 99999;        //服务器系统错误
    public static final int RESPONSE_ERROR_LOST_10007 = 10007;     //"查不到用户","10007"可以认为token无效

    private static AlertDialog mDialog;

    /**
     * 需要token的错误码工具方法(网络错误在这个方法只是打toast，无网络需要showEmptyView的界面不适合这个方法)
     *
     * @param errorCode 错误码
     * @param activity
     */
    public static boolean showHaveTokenError(Activity activity, String errorCode, String message) {
        if(activity == null) return false;
        //网络错误
        if (String.valueOf(NOT_NETWORK_400).equals(errorCode)) {
            ToastUtil.showShort(activity, "网络连接错误，请检查您的网络连接..");
            return true;
            //token过期,弹出登录对话框
        } else if (String.valueOf(TOKEN_OVERDUE_10001).equals(errorCode)) {
            gotoLogin(activity, "您长时间未登陆账号，请重新登录");
            return true;
            //查不到用户
        } else if (String.valueOf(RESPONSE_ERROR_LOST_10007).equals(errorCode)) {
            gotoLogin(activity, "您在多台设备登录账号，请重新登录");
            return true;
            //服务器系统错误
        } else if (String.valueOf(SERVER_ERROR_99999).equals(errorCode)) {
            ToastUtil.showShort(activity, "服务器系统错误");
            return true;
            //服务器繁忙
        } else if (String.valueOf(SERVER_ERROR).equals(errorCode)) {
            ToastUtil.showShort(activity, "服务器开小差了，请稍等");
            return true;
        } else {
            ToastUtil.showShort(activity, message );
            return false;
        }
    }

    /**
     * web页面交互错误
     *
     * @param activity
     * @param errorCode
     * @param requestCode
     * @return
     */
    public static boolean webError(Activity activity, String errorCode, int requestCode) {
        if(activity == null)    return false;

        if (String.valueOf(NOT_NETWORK_400).equals(errorCode)) {
            ToastUtil.showShort(activity, "网络连接错误，请检查您的网络连接..");
            return true;
            //token过期,弹出登录对话框
        } else if (String.valueOf(TOKEN_OVERDUE_10001).equals(errorCode)) {
            GotoUtil.goToActivityForResult(activity, LoginAndRegisteActivity.class, requestCode);
            return true;

        } else if (String.valueOf(TOKEN_NULL_10002).equals(errorCode)) {
            GotoUtil.goToActivityForResult(activity, LoginAndRegisteActivity.class, requestCode);
            return true;
        //查不到用户
        }else if (String.valueOf(RESPONSE_ERROR_LOST_10007).equals(errorCode)) {
            GotoUtil.goToActivityForResult(activity, LoginAndRegisteActivity.class, requestCode);
            return true;
            //服务器系统错误
        } else if (String.valueOf(SERVER_ERROR_99999).equals(errorCode)) {
            ToastUtil.showShort(activity, "服务器系统错误");
            return true;
            //服务器繁忙
        } else if (String.valueOf(SERVER_ERROR).equals(errorCode)) {
            ToastUtil.showShort(activity, "服务器开小差了，请稍等");
            return true;
        } else {
            ToastUtil.showShort(activity, "-错误码:" + errorCode);
            return false;
        }
    }

    public static void gotoLogin(final Activity activity, String content) {
        if(activity == null) return;
        if (mDialog != null) mDialog.dismiss();

        mDialog = DialogUtil.createDialog(activity, "登录账号", content, "取消",
                "确定", new DialogUtil.DialogClickListener() {
                    @Override
                    public void clickLeftButton(View view) {
                        mDialog.dismiss();
                        UserManager.getInstance().deleteUser();
                        EventBusUtil.post(new EventLoginBean(TOKEN_OVERDUCE));
                    }

                    @Override
                    public void clickRightButton(View view) {
                        UserManager.getInstance().deleteUser();
                        Intent intent = new Intent(activity, LoginAndRegisteActivity.class);
                        activity.startActivityForResult(intent, Constant.requestCode.NEWS_LIKEANDCOLLECT);
                        mDialog.dismiss();
                    }
                });
        mDialog.show();
    }
}
