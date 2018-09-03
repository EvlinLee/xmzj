package com.gxtc.commlibrary.utils;

import com.gxtc.commlibrary.BaseUiView;
import com.gxtc.commlibrary.BaseUserView;


/**
 * Created by 宋家任 on 2016/12/13.
 * 错误码工具类(处理公共的错误码)
 */

public class ErrorCodeUtil {

//    public static final int NO_NETWORK = 300;
    public static final int RESOLVE_ERROR = 401;
    public static final int SERVER_ERROR = 500;
    public static final int ON_NETWORK_400= 400;

    public static final int TOKEN_OVERDUE_10001 = 10001;         // token 过期
    public static final int NO_TOKEN_10002 = 10002;
    public static final int SERVER_ERROR_99999 = 99999;        //服务器系统错误
    public static final int RESPONSE_ERROR_LOST_10007 = 10007;     //"查不到用户","10007"可以认为token无效
    public static final int RESPONSE_ERROR_NO_FOUND = 10007;     //"查不到用户","10007"可以认为token无效
    public static final int service_err = 99999;   //服务器系统错误
    public static final int already_registed = 10003;      //手机号已经注册
    public static final int RESPONSE_ERROR_LOST_10004 = 10004;       //查询无信息
    public static final int RESPONSE_ERROR_USER__10023 = 10023;     //登录注册时的错误码 //手机号已注册


    private static int string2Int(String temp) {
        int code;
        try {
            code = Integer.valueOf(temp);
        } catch (Exception e) {
            code = SERVER_ERROR_99999;
        }
        return code;
    }


    /**
     * @param errorCode 错误码
     * @param view      view层接口
     */
    public static void handleErr(BaseUiView view, String errorCode, String msg) {
        if (view == null)   return;
        view.showLoadFinish();
        int code = string2Int(errorCode);
        switch (code) {
            case ON_NETWORK_400:
                view.showNetError();
                break;
            //服务器系统错误
            case SERVER_ERROR_99999:
                view.showError("服务器繁忙");
                break;

            //其他错误
            default:
                view.showError(msg);
                break;
        }

    }

    /**
     * 用于有token验证的情况
     *
     * @param errorCode 错误码
     * @param view      view层接口
     */
    public static void handleErr(BaseUserView view, String errorCode, String msg) {
        if (view == null)   return;
        view.showLoadFinish();
        int code = string2Int(errorCode);
        switch (code) {
            //网络错误
            case ON_NETWORK_400:
                view.showNetError();
                break;

            case SERVER_ERROR_99999:
                view.showError("服务器繁忙");
                break;

            //token过期,弹出登录对话框
            case TOKEN_OVERDUE_10001:
                view.tokenOverdue();
                break;
            //token为空
            case NO_TOKEN_10002:
                view.tokenOverdue();
                break;


            //除了登录 注册 界面外   其他界面查不到用户全都是多端登录
            case RESPONSE_ERROR_LOST_10007:
                view.tokenOverdue();
                break;

            //其他错误
            default:
                view.showError(msg);
                break;
        }

    }

}
