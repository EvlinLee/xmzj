package com.gxtc.commlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * 窗口帮助类
 */
public class WindowUtil {

    public static final int TYPE_WIDTH  = 1;    //屏幕宽度
    public static final int TYPE_HEIGHT = 2;    //屏幕高度

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public static int getScreenWidth(Context context) {
        return screenWidth == 0 ? getScreenSize(TYPE_WIDTH, context) : screenWidth;
    }


    /**
     * 获取屏幕的宽高
     *
     * @param type
     * @param context
     * @return -1表示参数错误
     */
    public static int getScreenSize(int type, Context context) {

        try {

            if (type == 1) {
                return getScreenW(context);
            } else if (type == 2) {
                return getScreenH(context);
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp(像素) 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class  clazz  = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(
                    clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * @param scaleW
     * @param scaleH
     * @param context
     * @return
     */
    public static float getScaleHeight(float scaleW,float scaleH,Context context){
        return getScaleHeight(scaleW, scaleH, -1, context);
    }

    /**
     * @param scaleW
     * @param scaleH
     * @param context
     * @return
     */
    public static float getScaleHeight(float scaleW,float scaleH,int width,Context context){
        if(context == null){
            return 0;
        }

        if(width == -1){
            width = getScreenSize(WindowUtil.TYPE_WIDTH, context);
        }
        float sw = scaleW;
        float sh = scaleH;
        return (width*sh)/sw;
    }


    //判断输入法是否打开并关闭输入法
    public static void closeInputMethod(Activity activity) {
        if (activity == null || activity.getBaseContext() == null) return;
        try {
            InputMethodManager imm    = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            boolean            isOpen = imm.isActive();
            if (isOpen && imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
    }

    //判断输入法是否打开并关闭输入法
    public static void closeInputMethod(View view,Context context) {
        if (context == null || view == null) return;
        try {
            InputMethodManager imm    = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            boolean            isOpen = imm.isActive();
            if (isOpen && imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 输入法是否弹出
     *
     * @param context
     * @return
     */
    public static boolean isInputOpen(Context context) {
        InputMethodManager imm    = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        boolean            isOpen = imm.isActive();
        return isOpen;
    }

    //判断输入法是否打开并关闭输入法
    public static void closeInputMethod(Context context, Activity activity) {
        if (activity == null) return;
        try {
            InputMethodManager imm    = (InputMethodManager) context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            boolean            isOpen = imm.isActive();
            if (isOpen && imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
    }


    /**
     * 返回屏幕可用高度
     * 当显示了虚拟按键时，会自动减去虚拟按键高度
     */
    public static int getAvailableScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }


    /**
     * 获取屏幕宽度
     */
    public static int getScreenW(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return screenWidth == 0? dm.widthPixels : screenWidth;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenH(Context aty) {
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        return screenHeight == 0? dm.heightPixels : screenHeight;
    }


}
