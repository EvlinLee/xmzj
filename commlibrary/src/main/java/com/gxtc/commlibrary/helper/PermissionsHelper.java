package com.gxtc.commlibrary.helper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.lang.ref.WeakReference;

/**
 * 来自 伍玉南 的装逼小尾巴 on 17/12/14.
 * 权限帮助类
 */

public class PermissionsHelper {

    private static PermissionsHelper mHelper;

    private WeakReference<Activity> mACReference;

    private WeakReference<PermissionsResultListener> mListenerReference;

    private PermissionsHelper(){

    }

    public static PermissionsHelper getInstance(Activity activity){
        if(mHelper == null){
            synchronized (PermissionsHelper.class){
                if(mHelper == null){
                    mHelper = new PermissionsHelper();
                    mHelper.mACReference = new WeakReference<>(activity);
                }
            }
        }
        return mHelper;
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

    private int mRequestCode;

    /**
     * 其他 Activity 继承 BaseActivity 调用 performRequestPermissions 方法
     *
     * @param desc        首次申请权限被拒绝后再次申请给用户的描述提示
     * @param permissions 要申请的权限数组
     * @param requestCode 申请标记值
     * @param listener    实现的接口
     */
    public void performRequestPermissions(String desc, String[] permissions, int requestCode, PermissionsResultListener listener) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mRequestCode = requestCode;
        mListenerReference = new WeakReference<>(listener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkEachSelfPermission(permissions)) {// 检查是否声明了权限
                requestEachPermissions(desc, permissions, requestCode);
            } else {// 已经申请权限
                if (mListenerReference.get() != null) {
                    mListenerReference.get().onPermissionGranted();
                }
            }
        } else {
            if (mListenerReference.get() != null) {
                mListenerReference.get().onPermissionGranted();
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
        if(getActivit() != null){
            // 需要再次声明
            if (shouldShowRequestPermissionRationale(permissions)) {
                showRationaleDialog(desc, permissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(getActivit(), permissions, requestCode);
            }
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
        if(getActivit() != null){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivit());
            builder.setTitle("提醒")
                   .setMessage(desc)
                   .setPositiveButton("允许", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           ActivityCompat.requestPermissions(getActivit(), permissions, requestCode);
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
    }


    /**
     * 再次申请权限时，是否需要声明
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        if(getActivit() != null){
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivit(), permission)) {
                    return true;
                }
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
        if(getActivit() != null){
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivit(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
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

    private Activity getActivit(){
        if(mACReference != null){
            return mACReference.get();
        }
        return null;
    }

}
