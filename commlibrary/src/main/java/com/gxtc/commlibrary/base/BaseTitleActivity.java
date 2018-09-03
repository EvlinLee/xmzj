package com.gxtc.commlibrary.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.gxtc.commlibrary.R;
import com.gxtc.commlibrary.utils.LightStatusBarUtils;
import com.gxtc.commlibrary.utils.RomUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class BaseTitleActivity extends SwipeBackActivity {

    private ViewStub viewStubBaseHead;           //标题栏
    private ViewStub viewStubBaseLoading;        //正在加载
    private ViewStub viewStubBaseEmpty;          //无数据

    private LinearLayout parentView;
    private FrameLayout contentArea;

    private LoadingView baseLoadingView;
    private EmptyView baseEmptyView;
    private HeadView baseHeadView;

    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private String curFragmentName = "";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //7.0透明状态栏会出现灰蒙
        if (isLightStatusBar() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.setContentView(R.layout.base_head_activity);

        mManager = getSupportFragmentManager();
        parentView = (LinearLayout) findViewById(R.id.parentArea);
        contentArea = (FrameLayout) findViewById(R.id.contentArea);
        viewStubBaseEmpty = (ViewStub) findViewById(R.id.view_stub_base_empty);
        viewStubBaseLoading = (ViewStub) findViewById(R.id.view_stub_base_loading);
        viewStubBaseHead = (ViewStub) findViewById(R.id.view_stub_base_head);

        LightStatusBarUtils.setLightStatusBar(this, isLightStatusBar());
    }


    @Override
    public void setContentView(int layoutResID) {
        View v = getLayoutInflater().inflate(layoutResID, contentArea, false);
        setContentView(v);

    }

    @Override
    public void setContentView(View view) {
        setContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentArea.addView(view, 0, params);
        ButterKnife.bind(this);

        initView();
        initListener();
        initData();
    }

    public void initView() {
    }

    public void initListener() {
    }

    public void initData() {
    }

    /**
     * 是否启用亮色状态栏模式
     */
    public boolean isLightStatusBar() {
        return true;
    }


    public void hideContentView() {
        View content = contentArea.getChildAt(0);
        if (content != null) {
            content.setVisibility(View.INVISIBLE);
        }
    }

    public void showContentView() {
        View content = contentArea.getChildAt(0);
        if (content != null) {
            content.setVisibility(View.VISIBLE);
        }
    }

    private Fragment mContent;

    public Fragment getCurrentFragment() {
        return mContent;
    }

    //启动沉浸式状态栏设置
    public void setImmerse() {
        if(Build.VERSION.SDK_INT == 19){
            getWindow().getDecorView() .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }else if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView() .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 替换fragment的方法
     *
     * @param fragment
     * @param simpleName
     * @param id
     */
    public void switchFragment(Fragment fragment, String simpleName, int id) {
        mTransaction = mManager.beginTransaction();
        curFragmentName = simpleName;
        if (fragment == null)
            return;

        if (mContent == null) {
            mTransaction.add(id, fragment, simpleName);
        } else {

            if (fragment.isAdded()) {
                mTransaction.hide(mContent).show(fragment);
            } else {
                mTransaction.hide(mContent).add(id, fragment, simpleName);
            }
        }
        mContent = fragment;
        mTransaction.commitAllowingStateLoss();
    }

    public LoadingView getBaseLoadingView() {
        if (baseLoadingView == null) {
            baseLoadingView = new LoadingView(viewStubBaseLoading.inflate());
        }
        return baseLoadingView;
    }

    public EmptyView getBaseEmptyView() {
        if (baseEmptyView == null) {
            baseEmptyView = new EmptyView(viewStubBaseEmpty.inflate());

        }
        return baseEmptyView;
    }

    public HeadView getBaseHeadView() {
        if (baseHeadView == null) {
            baseHeadView = new HeadView(viewStubBaseHead.inflate());
            setActionBarTopPadding(baseHeadView.getParentView(), false);



        }
        return baseHeadView;
    }

    public LinearLayout getParentView() {
        return parentView;
    }

    private void setStatusView() {
        Window window = getWindow();
        //这里是自己定义状态栏的颜色
        int color = getResources().getColor(R.color.status_bg);

        ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));

        // 5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //设置透明状态栏,这样才能让 ContentView 向上
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }

            // 4.4到5.0
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View statusBarView = mContentView.getChildAt(0);
            //移除假的 View
            if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                    statusBarView.getLayoutParams().height == getStatusBarHeight()) {
                mContentView.removeView(statusBarView);
            }
            //不预留空间
            if (mContentView.getChildAt(0) != null) {
                ViewCompat.setFitsSystemWindows(mContentView.getChildAt(0), false);
            }
        }
    }

    protected void setActionBarTopPadding(View v, boolean changeHeight) {
        switch (RomUtils.getLightStatausBarAvailableRomType()) {
            case RomUtils.AvailableRomType.MIUI:
            case RomUtils.AvailableRomType.FLYME:
            case RomUtils.AvailableRomType.ANDROID_NATIVE://6.0以上
                if (v != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        int stataHeight = getStatusBarHeight();
                        if (changeHeight) {
                            v.getLayoutParams().height = v.getLayoutParams().height + stataHeight;
                        }
                        v.setPadding(v.getPaddingLeft(),
                                stataHeight,
                                v.getPaddingRight(),
                                v.getPaddingBottom());

                    }
                }
                break;

            case RomUtils.AvailableRomType.NA://6.0以下

                break;
        }

    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        Window window;
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }


    public FrameLayout getContentArea() {
        return contentArea;
    }


    /**
     * =====================
     * 申请权限相关
     * =====================
     */
    public interface PermissionsResultListener {

        void onPermissionGranted();

        void onPermissionDenied();

    }


    private PermissionsResultListener mListener;

    private int mRequestCode;

    /**
     * 其他 Activity 继承 BaseActivity 调用 performRequestPermissions 方法
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    protected void performRequestPermissions(String desc, String[] permissions, int requestCode, PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    /**
     * 申请权限前判断是否需要声明
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private void requestEachPermissions(String desc, String[] permissions, int requestCode) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(desc, permissions, requestCode);
        } else {
            ActivityCompat.requestPermissions(BaseTitleActivity.this, permissions, requestCode);
        }
    }

    /**
     * 弹出声明的 Dialog
     *
     * @param desc
     * @param permissions
     * @param requestCode
     */
    private void showRationaleDialog(String desc, final String[] permissions, final int requestCode) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒")
                .setMessage(desc)
                .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(BaseTitleActivity.this, permissions, requestCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }


    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return true 需要申请权限,false 已申请权限
     */
    private boolean checkEachSelfPermission(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请权限结果的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mRequestCode) {
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            } else {// 用户拒绝申请权限
                if (mListener != null) {
                    mListener.onPermissionDenied();
                }
            }
        }
    }

    /**
     * 检查回调结果
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //统一处理关闭输入法
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();

            //如果不是落在EditText区域，则需要关闭输入法
            if (HideKeyboard(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private boolean HideKeyboard(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {

            int[] location = {0, 0};
            view.getLocationInWindow(location);

            //获取现在拥有焦点的控件view的位置，即EditText
            int left = location[0], top = location[1], bottom = top + view.getHeight(), right = left + view.getWidth();
            //判断我们手指点击的区域是否落在EditText上面，如果不是，则返回true，否则返回false
            boolean isInEt = (event.getX() > left && event.getX() < right && event.getY() > top
                    && event.getY() < bottom);
            return !isInEt;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
