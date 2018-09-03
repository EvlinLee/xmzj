package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.gxtc.commlibrary.utils.RomUtils;

import java.lang.reflect.Method;


public class AndroidBug5497Workaround {

    // For more information, see https://code.google.com/p/android/issues/detail?id=5497  
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.  

    public static int KEYBOARD_STATE_SHOW = 1;
    public static int KEYBOARD_STATE_HIDE = 0;

    private int stateHeight = 0;
    private int navigationBarHeight = 0;
    private KeyboardListener mListener;

    public static void assistActivity(Activity activity, KeyboardListener listener) {
        new AndroidBug5497Workaround(activity, listener);
    }

    public static void assistActivity(Activity activity) {

        new AndroidBug5497Workaround(activity, null);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int flag;

    private AndroidBug5497Workaround(Activity activity, KeyboardListener listener) {
        mListener = listener;

        if (RomUtils.getLightStatausBarAvailableRomType() == RomUtils.AvailableRomType.NA) {
            flag = 0;
        } else {
            flag = 1;
            FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
            mChildOfContent = content.getChildAt(0);
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            possiblyResizeChildOfContent();
                        }
                    });
            frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
        }
        getStateHeight(activity);
        getNavigationBarHeight(activity);

    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;

            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible  
                frameLayoutParams.height = (usableHeightSansKeyboard - heightDifference) + stateHeight;
                if (mListener != null)
                    mListener.keyboardStateChange(KEYBOARD_STATE_SHOW, frameLayoutParams.height);

            } else {
                // keyboard probably just became hidden  
                frameLayoutParams.height = usableHeightSansKeyboard - navigationBarHeight;
                if (mListener != null)
                    mListener.keyboardStateChange(KEYBOARD_STATE_HIDE, frameLayoutParams.height);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        if (r.top == 0) {
            r.top = stateHeight;//状态栏目的高度
        }
        return (r.bottom - r.top);
    }

    private void getStateHeight(Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId > 0) {
            if (flag == 1) {
                stateHeight = activity.getResources().getDimensionPixelSize(resourceId);
            } else {
                stateHeight = 0;
            }


        }

    }

    //获取虚拟按键栏高度
    private void getNavigationBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Resources resources = activity.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen",
                    "android");
            if (resourceId > 0 && hasNavBar(activity)) {
                if (flag == 1)
                    navigationBarHeight = resources.getDimensionPixelSize(resourceId);
                else {
                    navigationBarHeight = 0;
                }
            }
        }
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    private static boolean hasNavBar(Context context) {
        //先判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar ，vivo x6用判断虚拟按键的方法获取到的是错误的
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (hasMenuKey && hasBackKey) {
            // 做任何你需要做的,这个设备有一个导航栏
            return false;

        } else {
            boolean hasNavigationBar = false;
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool",
                    "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            try {
                Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method m = systemPropertiesClass.getMethod("get", String.class);
                String navBarOverride = (String) m.invoke(systemPropertiesClass,
                        "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    hasNavigationBar = false;
                } else if ("0".equals(navBarOverride)) {
                    hasNavigationBar = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hasNavigationBar;
        }

    }

    /**
     * 软键盘状态监听
     */
    public interface KeyboardListener {

        /**
         * 键盘的弹起或收起
         *
         * @param contentHeight 屏幕可见内容的高度
         */
        void keyboardStateChange(int state, int contentHeight);
    }

}  