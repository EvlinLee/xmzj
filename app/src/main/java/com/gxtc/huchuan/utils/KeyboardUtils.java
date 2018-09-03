package com.gxtc.huchuan.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/02
 *     desc  : 键盘相关工具类
 * </pre>
 */
public final class KeyboardUtils {

    private static int sContentViewInvisibleHeightPre;

    public static int KEYBOARD_OPEN = 1;
    public static int KEYBOARD_CLOSE = 0;

    private KeyboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /*
      避免输入法面板遮挡
      <p>在 manifest.xml 中 activity 中设置</p>
      <p>android:windowSoftInputMode="adjustPan"</p>
     */

    /**
     * 动态显示软键盘
     *
     * @param activity activity
     */
    public static void showSoftInput(final Activity activity) {
        if(activity != null){
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            View view = activity.getCurrentFocus();
            if (view == null) view = new View(activity);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 动态显示软键盘
     *
     * @param view 视图
     */
    public static void showSoftInput(Context context, final View view) {
        if(context != null && view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    public static void hideSoftInput(final Activity activity) {
        if(activity != null){
            InputMethodManager imm =
                    (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            View view = activity.getCurrentFocus();
            if (view == null) view = new View(activity);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 动态隐藏软键盘
     *
     * @param view 视图
     */
    public static void hideSoftInput(Context context, final View view) {
        if(context != null && view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 切换软键盘显示与否状态
     */
    public static void toggleSoftInput(Context context) {
        if(context != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) return;
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }


    /**
     * 判断软键盘是否可见
     * <p>默认软键盘最小高度为 200</p>
     *
     * @param activity activity
     * @return {@code true}: 可见<br>{@code false}: 不可见
     */
    public static boolean isSoftInputVisible(final Activity activity) {
        return getContentViewInvisibleHeight(activity) >= 200;
    }


    /**
     * 判断软键盘是否可见
     *
     * @param activity             activity
     * @param minHeightOfSoftInput 软键盘最小高度
     * @return {@code true}: 可见<br>{@code false}: 不可见
     */
    public static boolean isSoftInputVisible(final Activity activity,
                                             final int minHeightOfSoftInput) {
        return getContentViewInvisibleHeight(activity) >= minHeightOfSoftInput;
    }


    private static int getContentViewInvisibleHeight(final Activity activity) {
        if(activity != null){
            final View contentView = activity.findViewById(android.R.id.content);
            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            return contentView.getBottom() - r.bottom;
        }
        return 0;
    }

    /**
     * 注册软键盘改变监听器
     * @param activity activity
     * @param listener listener
     */
    private static int usableHeightPrevious;

    public static void registerSoftInputChangedListener(final Activity activity, final OnSoftInputChangedListener listener) {
        if (activity == null) return;
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                int usableHeightNow = computeUsableHeight(activity, contentView);
                if (usableHeightNow != usableHeightPrevious) {
                    int usableHeightSansKeyboard = contentView.getRootView().getHeight();
                    int heightDifference = usableHeightSansKeyboard - usableHeightNow;

                    if (heightDifference > (usableHeightSansKeyboard / 4)) {
                        // keyboard probably just became visible
                        if(listener != null)
                            listener.onSoftInputChangeState(KEYBOARD_OPEN);

                    } else {
                        // keyboard probably just became hidden
                        if(listener != null)
                            listener.onSoftInputChangeState(KEYBOARD_CLOSE);

                    }
                    contentView.requestLayout();
                    usableHeightPrevious = usableHeightNow;
                }
            }
        });
    }


    private static int computeUsableHeight(Activity activity, View contentView) {
        if(activity != null && contentView != null){
            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            if (r.top == 0) {
                r.top = getStateHeight(activity);//状态栏目的高度
            }
            return (r.bottom - r.top);
        }
        return 0;
    }


    private static int getStateHeight(Activity activity){
        if(activity != null){
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return activity.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return  0;
    }


    /**
     * 点击屏幕空白区域隐藏软键盘
     * <p>根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘</p>
     * <p>需重写 dispatchTouchEvent</p>
     * <p>参照以下注释代码</p>
     */
    public static void clickBlankArea2HideSoftInput() {
        Log.i("KeyboardUtils", "Please refer to the following code.");
        /*
        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideKeyboard(v, ev)) {
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS
                    );
                }
            }
            return super.dispatchTouchEvent(ev);
        }

        // 根据 EditText 所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
        private boolean isShouldHideKeyboard(View v, MotionEvent event) {
            if (v != null && (v instanceof EditText)) {
                int[] l = {0, 0};
                v.getLocationInWindow(l);
                int left = l[0],
                        top = l[1],
                        bottom = top + v.getHeight(),
                        right = left + v.getWidth();
                return !(event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom);
            }
            return false;
        }
        */
    }

    public interface OnSoftInputChangedListener {
        void onSoftInputChangeState(int state);
    }
}
